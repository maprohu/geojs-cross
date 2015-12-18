package com.github.maprohu.geojs

trait Geometry
case class Point(lat: Double, lon: Double) extends Geometry
case class LineString(points: Seq[Point]) extends Geometry
case class Polygon(points: Seq[Point]) extends Geometry
