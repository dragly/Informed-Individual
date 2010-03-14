from django.db import models
from django.contrib.auth.models import User

# Create your models here.

"""
An identifier is something one may have opinions about. It could be an URL, a barcode etc.
"""
class Identifier(models.Model):
	type = models.CharField(max_length=200)
	data = models.TextField()
	def __unicode__(self):
		return self.type + " [" + self.data + "]"
"""
An Entity is something that is defined. An Entity may have many Identifiers, and to connect these, we use Connections.
"""
class Entity(models.Model):
	name = models.CharField(max_length=200)
	description = models.TextField()
	def __unicode__(self):
		return self.name
"""
A Connection gives the relation between an Entity and an Identifier.
For instance: The barcode 12345 is for Nuke Cola. Thus, an identifier with
type "EAN" and data "12345" would have connections to Nuke Cola (the product)
and Nuke (the company). Nuke Cola would as well have a connection to Nuke (the company).

NOTE: This model is subject to future changes.
"""
class Connection(models.Model):
    entity = models.ForeignKey(Entity)
    identifier = models.ForeignKey(Identifier)
    affection = models.FloatField() # how much an opinion about the entity or identifier should affect the other (not in use yet)
    description = models.CharField(max_length=200) # just a string which says in short how these are related
    # NOTE: The affection should maybe be removed and replaced by the score an opinion gives about an connection
    def __unicode__(self):
        return self.identifier.__unicode__() + " => " + self.entity.__unicode__()
"""
A user is a user, but "User" is a bad table name.
"""
class UserProfile(models.Model):
#	email = models.CharField(max_length=200)
#	password = models.CharField(max_length=200)
	displayname = models.CharField(max_length=200)
	user = models.ForeignKey(User, unique=True)
	def __unicode__(self):
		return self.displayname
"""
Opinions are given about identifiers by users.
"""
class Opinion(models.Model):
	score = models.FloatField()
	description = models.TextField()
	identifier = models.ForeignKey(Identifier)
	user = models.ForeignKey(User)
	def __unicode__(self):
		return self.user.__unicode__() + " - " + self.identifier.__unicode__()
"""
A trustnetwork may exist independent or dependent to a user.
This makes it possible to get Opinions about Identifiers without creating a Login.
"""
class TrustNetwork(models.Model):
	user = models.ForeignKey(User)
	active = models.BooleanField() # active is true if this is the newest network for a user
#users = models.ManyToManyField(Login)
	def __unicode__(self):
		return self.user.__unicode__()

"""
A TrustNetwork consists of TrustNodes as a kind of ManyToMany mapping table.
It is created into an own table in case each node should have certain properties,
such as how much it's trusted.
"""
class TrustNode(models.Model):
	user = models.ForeignKey(User)
	network = models.ForeignKey(TrustNetwork)
	score = models.FloatField(default=0)
	def __unicode__(self):
		return self.user.__unicode__() + " in " + self.network.__unicode__()
"""
The result is a calculated object telling the user what his/hers network thinks about an Identifier
"""
class Result(models.Model):
	score = models.FloatField()
	identifier = models.ForeignKey(Identifier)
	network = models.ForeignKey(TrustNetwork)
	active = models.BooleanField() # active is true if this is the most recent generated result
	def __unicode__(self):
		return "Score: " + str(self.score) + ", " + self.identifier.__unicode__() + " " + self.network.__unicode__() + ", active: " + str(self.active)
