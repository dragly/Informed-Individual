from piston.handler import BaseHandler
from piston.utils import rc, throttle
from informi.models import *
from django.contrib.auth.models import User


#class LoginHandler(BaseHandler):
#        model = Login
#        fields = ('displayname','id',)
#           #exclude = ('email','password',)
#        allowed_methods = ('GET',)
#        def read(self, request, user_id):
#                user = Login.objects.get(pk=user_id)
#                return user


class OpinionHandler(BaseHandler):
        model = Opinion
        fields = ("id", "score", "description", "identifier", "user")
        allowed_methods = ('GET','POST','PUT','DELETE',)
        def read(self, request, method=None, param=None):
            print "-- in OpinionHandler.read --"
	    if method == "show":
                if param == None:
		    if "identifier_type" in request.GET and "identifier_data" in request.GET:
			identifierType = request.GET.get("identifier_type")
			identifierData = request.GET.get("identifier_data")
			print "Looking up: ", identifierType, " = ", identifierData
			identifiers = Identifier.objects.filter(type=identifierType, data=identifierData)
			if identifiers.count() > 0:
			    opinions = Opinion.objects.filter(identifier=identifiers[0], user=request.user)
			    return opinions[0]
			else:
			    return {"error" : "Identifier not found"}
                    else:
			return {"opinions" : Opinion.objects.filter(user=request.user)} # if nothing has been defined, show all our own opinions
                else:
                        opinion = Opinion.objects.get(pk=param)
                        return opinion
        def create(self, request, method=None, param=None):
            print "-- in OpinionHandler.create --"
	    if method == "create":
		try:
		    score = request.POST.get("score")
		    if float(score) > 1 or float(score) < 0:
			return {"error":"Score must be between 0 and 1.","errorCode":"1008"}
		    description = request.POST.get("description")
		    identifierType = request.POST.get("identifier_type")
		    identifierData = request.POST.get("identifier_data")
		    identifiers = Identifier.objects.filter(type=identifierType, data=identifierData)
		    if identifiers.count() == 0: # if this is a new identifier, create it
			identifier = Identifier(type=identifierType, data=identifierData)
			identifier.save()
		    else:
			identifier = identifiers[0]
		    opinions = Opinion.objects.filter(identifier=identifier, user=request.user)
		    if opinions.count() > 0:
			return {"error":"Login already has existing opinion about this product.","errorCode":"1001"}
		    else:
			opinion = Opinion(score=score, description=description, identifier=identifier, user=request.user)
		    opinion.save()
		    return opinion
		except KeyError:
		    return {"error":"Not all attributes defined"}
        def update(self, request, method=None, param=None):
            print "-- in OpinionHandler.create --"
            try:
                if param == None:
                    return {"error":"Opinion id not specified","errorCode":"1002"}
                else:
                    opinion = Opinion.objects.get(pk=param)
                    if not opinion.user == request.user:
                        resp = rc.FORBIDDEN
                        resp.write(" You are not the owner of this opinion.")
                        return resp
                    score = request.POST.get("score")
                    description = request.POST.get("description")
                    identifierType = request.POST.get("identifier_type", None)
                    identifierData = request.POST.get("identifier_data", None)
                    if identifierType != None:
			identifiers = Identifier.objects.filter(type=identifierType,data=identifierData)
			if identifiers.count() < 1:
			    identifier = Identifier(type=identifierType, data=identifierData)
			    identifier.save()
			else:
			    identifier = identifiers[0]
			opinion.identifier = identifier
                    opinion.score = score
                    opinion.description = description
                    opinion.save()
                    return opinion
            except KeyError:
                return {"error":"Not all attributes defined"}
        def delete(self, request, method=None, param=None):
            if param == None:
                return {"error":"Opinion id not specified","errorCode":"1002"}
            else:
                opinion.objects.get(pk=param)
                if not opinion.user == request.user:
                    return rc.FORBIDDEN
                opinion.delete()
                return rc.DELETED
    
class UserHandler(BaseHandler):
    model = User
    fields = ("username","first_name","last_name","id",)
    def read(self, request, method=None):	    
	if method == "search":
	    if "q" in request.GET:
		query = request.GET.get("q")
		users = User.objects.filter(username__icontains=query)
		usersWithData = []
		for user in users:
		    userWithData = {}
		    userWithData["in_network"] = False
		    userWithData["score"] = 0.0
		    userWithData["username"] = user.username
		    userWithData["first_name"] = user.first_name
		    userWithData["last_name"] = user.last_name
		    userWithData["id"] = user.id
		    networks = request.user.trustnetwork_set.filter(active=True)
		    network = networks[0]
		    nodes = network.trustnode_set.all()
		    for node in nodes: # go through the current users network and see if the user is already there
			if node.user == user:
			    userWithData["in_network"] = True
			    userWithData["score"] = node.score
		    usersWithData.append(userWithData)
		return {"users" : usersWithData}
	    else:
		{"error" : "unknown query"}
	else:
	    {"error" : "unknown query"}
class AccountHandler(BaseHandler):
    def read(self, request, method=None):
	if method == "is_authenticated":
	    if request.user.is_authenticated():
		return request.user
	    else:
		resp = rc.FORBIDDEN
		resp.write({"Error" : "Forbidden"})
		return resp
	else:
	    return {"error" : "unknown method"}
"""
A result is a calculated opinion.

It is calculated by looking for all opinions in the trust network. Each opinion is weighted in the
following way:

- if the opinion is outside the current trust network it does not count.
- find the shortest trust path from the opinion user to the current user
- for each step in the path get the product of the trust (step1 * step2 * step3 ...)
- stop checking the path if the trust product falls below 0.5
- when all opinions are given a weight, sum all these up and divide each weight on the sum to find the relative weight
- multiply each relative weight with each opinion score and sum these

This function returns the final sum.

TODO: Implement this function completely.

NOTE: Maybe it should also return a number giving an indication of how trustful the information is.
"""
class ResultHandler(BaseHandler):
        model = Result
        fields = ("score",)
        allowed_methods = ('GET','PUT',)
        def read(self, request, method=None, param=None):
                print "-- in ResultHandler.read --"
                identifier_type = request.GET.get("identifier_type")
                identifier_data = request.GET.get("identifier_data")
                print identifier_data
                print identifier_data.encode('iso8859-1')
                if identifier_type == None or identifier_data == None:
                        return {"error" : "No identifier defined"}
                else:
                        if request.user.is_authenticated():
                            allopinions = []
                            opinionList = []
			    identifiers = Identifier.objects.filter(type=identifier_type,data=identifier_data)
			    if identifiers.count() < 1:
				identifier = Identifier(type=identifier_type, data=identifier_data)
				identifier.save()
			    else:
				identifier = identifiers[0]
                            sum = 0.0
                            i = 0.0
                            opinions = Opinion.objects.filter(identifier=identifier)
                            totalTrust = 0.0
                            # TODO: This should be calculated only if the network is redefined.
                            alreadyChecked = []
			    toCheck = []
			    toCheck.append(request.user)
			    trustInUser = {}
			    trustInUser[request.user] = 1.0 # we of course trust ourselves a lot! :)
			    for checkUser in toCheck: # loop through all users in our network to find our trust in them
				print "Checking: " + str(checkUser)
				checkNetworks = checkUser.trustnetwork_set.filter(active=True)
				if checkNetworks.count() > 0:
				    checkNetwork = checkNetworks[0]
				    nodes = checkNetwork.trustnode_set.all()
				    for node in nodes:
					print "Found new node: " + str(node.user)
					print str(checkUser) + " trusts " + str(node.user) + " with " + str(node.score)
					if node.user not in alreadyChecked and node.user not in toCheck:
					    trustInUser[node.user] = trustInUser[checkUser] * node.score
					    toCheck.append(node.user)
					    print "You (" + str(request.user) + ") now trust " + str(node.user) + " as much as: " + str(trustInUser[node.user])
				toCheck.remove(checkUser)
				alreadyChecked.append(checkUser)
				
                            for opinion in opinions: # calculate for each opinion about this identifier
				print "Opinion " + str(opinion.id) + " by " + str(opinion.user)
                                if opinion.user in trustInUser: # check if we trust this guy somehow
				    opinionTrust = trustInUser[opinion.user]
                                else: # do not add to the sum if there was no reason to trust this guy
				    opinionTrust = 0
				allopinions.append([opinion, opinionTrust])
				totalTrust += opinionTrust
				print "Trust: ", opinionTrust, "Score: ", opinion.score, "User: ", opinion.user.username
				opinionList.append({"trust" : opinionTrust, "score" : opinion.score, "description" : opinion.description, "user" : {"username" : opinion.user.username, "user_id" : opinion.user.id}})
                            
			    # now we iterate through all the opinions to set their relative weight
			    weightedOpinions = []
			    for op in allopinions:
				opinion = op[0]
				relativeTrust = op[1] / totalTrust # the relative amount of trust
				sum += opinion.score * relativeTrust # the sum of scores is calculated by multiplying each trust with each score
				i += 1
			    if i > 0:
                                    score = sum
                            else:
                                    score = -1
                            myNetworks = request.user.trustnetwork_set.all()
                            myNetwork = myNetworks[0]
                            results = Result.objects.all()
                            results.update(active=False) # TODO: Remove this!
                            result = Result(network=myNetwork, identifier=identifier, score=score, active=True)
                            result.save()
                            return {"identifier" : identifier, "result" : result, "opinions" : opinionList}
                        else:
                            return {"error":"Not authenticated!"}
                #        return {"Kake":"Bake", "sake":{"lake":"kake","make":{"fake":"take"}}} # custom return
        #def update(self, request, network_id=None, identifier_id=None):
        #        results = Result.objects.filter(network=network_id, identifier=identifier_id)
        #        result = results[0]
        #        result.score = request.PUT.get("score")
        #        result.save()
        #        
        #        return result
class TrustNetworkHandler(BaseHandler):
    model = TrustNetwork
    fields = ('id','user','active',)
    def read(self, request, method=None, param=None):
	if method == "show":
	    if param == None:
		networks = request.user.trustnetwork_set.filter(active=True)
		network = networks[0]
	    else:
		network = TrustNetwork.objects.get(pk=network_id)
	    nodes = network.trustnode_set.all()
	    return {"network" : network, "nodes" : nodes}
	else:
	    return {"error" : "Unknown method"}
	
class TrustNodeHandler(BaseHandler):
    model = TrustNode
    fields = ('user','score',)
    def create(self, request, method=None, param=None):
	if request.META.get("HTTP_X_HTTP_METHOD_OVERRIDE") == "DELETE":
	    return self.remove(request, method, param)
	user_id = request.POST.get("user_id")
	score = request.POST.get("score")
	networks = request.user.trustnetwork_set.filter(active=True)
	network = networks[0]
	user = User.objects.get(pk=user_id)
	trustnode = TrustNode(user=user, score=score, network=network)
	trustnode.save()
	return trustnode
    def update(self, request, method=None, param=None):
	user_id = param
	score = request.POST.get("score")
	networks = request.user.trustnetwork_set.filter(active=True)
	network = networks[0]
	user = User.objects.get(pk=user_id)
	trustnodes = TrustNode.objects.filter(user=user, network=network)
	trustnode = trustnodes[0]
	trustnode.score = score
	trustnode.save()
	return trustnode
    def delete(self, request, method=None, param=None):
	return self.remove(request, method, param)
    def remove(self, request, method=None, param=None):
	user_id = param
	user = User.objects.get(pk=user_id)
	networks = request.user.trustnetwork_set.filter(active=True)
	network = networks[0]
	trustnode = TrustNode.objects.filter(user=user, network=network)
	trustnode.delete()
	return rc.DELETED
	