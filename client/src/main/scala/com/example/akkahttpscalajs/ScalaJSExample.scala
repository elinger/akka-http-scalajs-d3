package com.example.akkahttpscalajs

import com.example.akkahttpscalajs.shared.Circle
import d3v4.d3
import d3v4.d3._
import org.scalajs.dom.ext.Ajax

import scala.scalajs.concurrent.JSExecutionContext.Implicits._
import scala.scalajs.js

object ScalaJSExample {

  def main(args: Array[String]): Unit = {
    println("Main")

    loadData(circles => {
      drawScene(circles)
    })
  }

  def loadData(f: js.Array[Circle] => Unit): Unit = {
    Ajax.get("/assets/items.json").onComplete { xhr =>
      if (xhr.isSuccess) {
        println("Loading json success")
        for {
          xhr <- xhr
          responseText = xhr.responseText
        } {
          val json = js.JSON
            .parse(
              responseText
            )
          val circlesArray = json.circles.asInstanceOf[js.Array[js.Dynamic]]
          val circles = for (circle <- circlesArray)
            yield {
              println(circle)
              Circle(
                name = circle.name.toString,
                x = circle.x.toString.toInt,
                y = circle.y.toString.toInt,
                r = circle.r.toString.toInt,
              )
            }
          f(circles)
        }
      } else {
        println("Loading json failed")
      }
    }
  }

  def drawScene(circles: js.Array[Circle]): Unit = {

    val svg = d3
      .select("body")
      .append("svg")
      .attr("width", "50%")
      .attr("height", "50%")
      .attr("transform", "translate(10,10)")


    val g = svg
      .selectAll(".someClass")
      .data(circles)
      .enter()
      .append("g")
      .attr("class", "someClass")
      .attr("transform", (d: Circle) => {
        "translate(" + d.x + "," + d.y + ")"
      })

    var last = System.currentTimeMillis()
    g.append("circle")
      .attr("r", (d: Circle) => d.r)
      .style("fill","#69b3a2")

    g.append("text")
      .text((d: Circle) => d.name)
  }
}
