
Introduction
============

This project was created during the CleanWeb Hackathon 2013 in Beijing.

The goal is to analyze the taxi dataset for find area in Beijing with high
potential for pedestrial development.

This program use Spark, a distributed computing framework written in Scala.

Team
====

* Bingyue
* Florian
* Laura
* Ludovic
* Martin
* Ray
* Sam
* Yao

Processing
==========

The program goes as as follow:
	a) read the input file
	b) parse the text format into binary format (Sample objects)
	c) filter the events "get in" and "get out" a taxi 
	d) join the consecutive events (get in and get out) into "trip" 
	e) and measure the distance of the trip
	e) filter the trips shorter than 3km.
	f) group the departure and arrival points in 10 clusters
	g) for each cluster, print 30 points (for visualization)

Input will be read from the file sample.csv

Output will be saved into:
	* ./depart.txt/part-XXXX : for the positions of the clusters
	* ./groups.txt/part-XXXX : for some departure/arrival points of the
	  clusters


Build
=====

Install sbt (Simple Build Tool) and execute:

	$ sbt package 

Run
===

	$ sbt run

