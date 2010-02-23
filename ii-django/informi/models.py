from django.db import models

# Create your models here.

"""
A product is something one may have opinions about.
"""
class Product(models.Model):
	name = models.CharField(max_length=200)
	description = models.CharField(max_length=200)
	eancode = models.CharField(max_length=200)
	def __unicode__(self):
		return self.name + " [" + self.eancode + "]"
"""
A login is a user, but "User" is a bad table name.
"""
class Login(models.Model):
	email = models.CharField(max_length=200)
	password = models.CharField(max_length=200)
	displayname = models.CharField(max_length=200)
	def __unicode__(self):
		return self.email
	#trusts = models.ManyToManyField(Trust)
	#opinions = models.ManyToManyField(Opinion)

"""
Opinions are given about products by logins.
"""
class Opinion(models.Model):
	score = models.FloatField()
	description = models.TextField()
	product = models.ForeignKey(Product)
	login = models.ForeignKey(Login)
	def __unicode__(self):
		return self.login.__unicode__() + " - " + self.product.__unicode__()
"""
A trustnetwork may exist independent or dependent to a login.
This makes it possible to get opinions about a product without creating a login.
"""
class TrustNetwork(models.Model):
	login = models.ForeignKey(Login)
	active = models.BooleanField() # active is true if this is the newest network for a login
#logins = models.ManyToManyField(Login)
	def __unicode__(self):
		return self.login.__unicode__()

"""
A TrustNetwork consists of TrustNodes as a kind of ManyToMany mapping table.
It is created into an own table in case each node should have certain properties,
such as how much it's trusted.
"""
class TrustNode(models.Model):
	login = models.ForeignKey(Login)
	network = models.ForeignKey(TrustNetwork)
	def __unicode__(self):
		return self.login.__unicode__() + " in " + self.network.__unicode__()
"""
The result is a calculated object telling the user what his/hers network thinks about a product
"""
class Result(models.Model):
	score = models.FloatField()
	product = models.ForeignKey(Product)
	network = models.ForeignKey(TrustNetwork)
	active = models.BooleanField() # active is true if this is the most recent generated result
	def __unicode__(self):
		return "Score: " + str(self.score) + ", " + self.product.__unicode__() + " " + self.network.__unicode__() + ", active: " + str(self.active)
