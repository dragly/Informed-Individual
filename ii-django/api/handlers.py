from piston.handler import BaseHandler
from informi.models import *


class LoginHandler(BaseHandler):
	model = Login
	fields = ('displayname','id',)
   	#exclude = ('email','password',)
	allowed_methods = ('GET',)
	def read(self, request, login_id):
		login = Login.objects.get(pk=login_id)
		return login


class OpinionHandler(BaseHandler):
	model = Opinion
	#fields = ('displayname',)
   	#exclude = ('email','password',)
	allowed_methods = ('GET',)
	def read(self, request, opinion_id=None):
		if opinion_id == None:
			return Opinion.objects.all()
		else:
			opinion = Opinion.objects.get(pk=opinion_id)
			return opinion

class ResultHandler(BaseHandler):
	model = Result
	def read(self, request, network_id=None, product_id=None):
		if network_id == None:
			return Result.objects.all()
		else:
#			try:
#				result = Result.objects.get(network=network_id, product=product_id)
#			except Result.DoesNotExist:
			results = Result.objects.filter(network=network_id, product=product_id)
			network = TrustNetwork.objects.get(pk=network_id)
			product = Product.objects.get(pk=product_id)
			sum = 0.0
			i = 0
			for node in network.trustnode_set.all():
				opinions = node.login.opinion_set.filter(product=product_id)
				if opinions.count() > 0:
					sum += opinions.get().score
					i += 1
			if i > 0:
				score = sum / i
			else:
				score = -1
			results.update(active=False)
			result = Result(network=network, product=product, score=sum, active=True)
			result.save()
			return result
