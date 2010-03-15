from django.conf.urls.defaults import *
from piston.resource import Resource
from piston.authentication import HttpBasicAuthentication
from piston.authentication import OAuthAuthentication
from piston.authentication import NoAuthentication

from ii.api.handlers import *

#auth = HttpBasicAuthentication(realm="Informed Individual")
auth = OAuthAuthentication(realm="Informed Individual")
#auth = NoAuthentication()
ad = { 'authentication': auth }

#login_resource = Resource(handler=LoginHandler)
opinion_resource = Resource(handler=OpinionHandler, **ad)
result_resource = Resource(handler=ResultHandler, **ad)
trustNetwork_resource = Resource(handler=TrustNetworkHandler, **ad)
trustNode_resource = Resource(handler=TrustNodeHandler, **ad)
user_resource = Resource(handler=UserHandler, **ad)
account_resource = Resource(handler=AccountHandler, **ad)
oneParamString = '(?P<method>[^/]+)/(?P<param>[^/]+)\.(?P<emitter_format>[^/]+)$'
noParamString = '(?P<method>[^/]+)\.(?P<emitter_format>[^/]+)$'

urlpatterns = patterns('piston.authentication',
    url(r'^oauth/request_token/$','oauth_request_token'),
    url(r'^oauth/authorize/$','oauth_user_auth'),
    url(r'^oauth/access_token/$','oauth_access_token'),
    url(r'^opinion/' + noParamString , opinion_resource),
    url(r'^opinion/' + oneParamString, opinion_resource),
    url(r'^result/' + noParamString , result_resource),
    url(r'^result/' + oneParamString, result_resource),
    url(r'^network/' + noParamString, trustNetwork_resource),
    url(r'^network/' + oneParamString, trustNode_resource ),
    url(r'^users/' + noParamString, user_resource ),
    url(r'^users/' + oneParamString, user_resource ),
    url(r'^trustnode/' + noParamString, trustNode_resource ),
    url(r'^trustnode/' + oneParamString, trustNode_resource ),
    url(r'^account/' + noParamString, account_resource ),
    url(r'^account/' + oneParamString, account_resource ),
)