package uk.ac.aber.adk15.controllers

import scalafxml.core.macros.sfxml

@sfxml
class ApplicationController {

  def start(): Unit = Console.out.println("Hello")
}