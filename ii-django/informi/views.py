from django.http import HttpResponse
#from django.template import RequestContext
#from django.http import Http404
from django.shortcuts import render_to_response, get_object_or_404
from ii.informi.models import *
from django.core import serializers

# Create your views here.
def detail(request, login_id):
	#l = Login.objects.get(pk=login_id)
	data = serializers.serialize("xml", Login.objects.all())
	return HttpResponse(data);
	#return HttpResponse("You're looking at  %s." % login_id)
	
def oauth_callback(request, other):
    html = "<html><body><p>The application is now. Type the following verification code into your application:<br/><input type=text value=\"%s\" /></body></html>" % other.verifier
    return HttpResponse(html)
