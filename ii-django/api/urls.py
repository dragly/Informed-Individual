from django.conf.urls.defaults import *
from piston.resource import Resource
from piston.authentication import HttpBasicAuthentication

from ii.api.handlers import *

login_resource = Resource(handler=LoginHandler)
opinion_resource = Resource(handler=OpinionHandler)
urlpatterns = patterns('',
    url(r'^login/(?P<login_id>[^/]+)/$', login_resource), 
    url(r'^opinion/$', opinion_resource),
    url(r'^opinion/(?P<opinion_id>[^/]+)/$', opinion_resource),
	url(r'^result/$', Resource(handler=ResultHandler)),
	url(r'^result/(?P<network_id>[^/]+)/(?P<product_id>[^/]+)/$', Resource(handler=ResultHandler)),
)
