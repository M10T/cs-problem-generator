package app

import model._
import scalatags.Text.all._
import scala.io.Source
import cask.Response
import cask.Request

import scala.collection.mutable.ArrayBuffer
import scalatags.generic.TypedTag

object MinimalApplication extends cask.MainRoutes{
  @cask.get("/")
  def welcome() = {
        doctype("html")(
    html(
      body(
        div(b("Welcome to CS Problem Generator!"),
        br()),
        form(action:="/problemTypeSelector", method:="GET")(
          input(attr("type"):="radio", attr("id"):="problemTypeOne", attr("name"):="problemType", attr("value"):="trace"),
          label(attr("for"):="problemTypeOne")("Problem Type One: Tracing"),
          br(),
          input(attr("type"):="radio", attr("id"):="randomCode", attr("name"):="problemType", attr("value"):="randomCode"),
          label(attr("for"):="randomCode")("Random Code Generator"),
          br(),
          input(attr("type"):="radio", attr("id"):="mathCode", attr("name"):="problemType", attr("value"):="mathCode"),
          label(attr("for"):="mathCode")("Random Math Code Generator"),
          br(),
          input(attr("type"):="radio", attr("id"):="stringCode", attr("name"):="problemType", attr("value"):="stringCode"),
          label(attr("for"):="mathCode")("Random String Code Generator"),
          br(),
          br(), 
          input(attr("type"):="radio", attr("id"):="multipleChoice", attr("name"):="responseType", attr("value"):="multipleChoice"),
          label(attr("for"):="multipleChoice")("Multiple Choice"),
          br(),
          input(attr("type"):="radio", attr("id"):="freeResponse", attr("name"):="responseType", attr("value"):="freeResponse"),
          label(attr("for"):="freeResponse")("Free Response"),
          br(),
          input(attr("type"):="radio", attr("id"):="mixedResponseTypes", attr("name"):="responseType", attr("value"):="mixedResponseTypes"),
          label(attr("for"):="mixedResponseTypes")("Mixed Response Types"),
          br(),
          br(),
          input(attr("type"):="number", attr("id"):="numberOfProblems", attr("name"):="numberOfProblems", attr("value"):="1", attr("min"):="1"),
          input(attr("type"):="submit", attr("id"):="submit"),
        )
      )
    ))
  }

  @cask.get("/problemTypeSelector")
  def problemTypeSelector(problemType: String, responseType: String = "multipleChoice", numberOfProblems: Int = 1) = problemType match
    case "randomCode" => cask.Response(randomCode())
    case "trace" => cask.Response(trace(responseType, numberOfProblems))
    case "mathCode" => cask.Response(mathTest())
    case "stringCode" => cask.Response(stringTest())
    case _ => cask.Response(html(), 401)
  
  @cask.get("/mathCode")
  def mathTest() = {
    val model = MathGenerator.randomGenerate()
    val translated = JavaTranslator.translateModel(model)
    val executedContext = CodeExecutor.executeModel(model)
    val displayed = executedContext.getDisplayed
    val data = executedContext.getData()
    html(
      body(
        div(b("Code:"),
          translated.split("\n").map(x=>p(x))),
        br(),
        p(b("Displayed: "),displayed.toString),
        p(b("Data: "),data.toString)
      )
    )
  }

  @cask.get("/stringCode")
  def stringTest() = {
    val translated = JavaTranslator.translateModel(StringGenerator.randomGenerate())
    html(
      body(
        div(b("Code:"),
          translated.split("\n").map(x=>p(raw(x.replace("\t","&emsp;"))))),
      )
    )
  }

  @cask.get("/customFunctions")
  def customFunctions() = {
    val functions = List(CustomMethods.doubleModel)
    var translated : String = JavaTranslator.translateFunction(CustomMethods.doubleModel)
    html (
      body (
        div(b("Code:")),
        functions.map(translated=>JavaTranslator.translateFunction(translated).split("\n")
          .map(x=>p(raw(x.replace("\t","&emsp;"))))),
      )
    )
  }

  @cask.get("/randomCode") 
  def randomCode() = {
    val model = JavaTranslator.randomGenerate()
    val translated = JavaTranslator.translateModel(model)
    val executedContext = CodeExecutor.executeModel(model)
    val displayed = executedContext.getDisplayed
    val data = executedContext.getData()
    html(
      body(
        div(b("Code:"),
          translated.split("\n").map(x=>p(raw(x.replace("\t","&emsp;"))))),
        br(),
        p(b("Displayed: "),displayed.toString),
        p(b("Data: "),data.toString)
      )
    )
  }

  @cask.staticFiles("/static")
  def staticFileRoutes() = "/src/main/resources"

  @cask.get("/trace")
  def trace(responseType: String, numberOfProblems: Int = 1) = {
    val r = scala.util.Random
    var problems = Array.ofDim[String](numberOfProblems, 7)

    for (i <- 1 to numberOfProblems){
      val model = JavaTranslator.randomGenerate()
      val translated = JavaTranslator.translateModel(model)
      val executedContext = CodeExecutor.executeModel(model)
      val displayed = executedContext.getDisplayed
      val data = executedContext.getData()
      val line = if displayed.isEmpty then 1 else r.nextInt(displayed.size) + 1
      val answerIndex = r.nextInt(4) + 1
      val answer = if displayed.isEmpty then "N/A" else s"${displayed(line-1)}"

      problems(i-1)(0) = translated
      problems(i-1)(5) = line.toString()
      problems(i-1)(6) = answerIndex.toString() // 7th element = line number of answer 

      for(j <- 1 to 4){
        if (j == answerIndex) {
          problems(i-1)(answerIndex) = answer
        } else {
          problems(i-1)(j) =  JavaString.randomGenerate()
        }
      }
    }

    var problemsString = ""
    for (i <- 1 to numberOfProblems){
      problemsString += problems(i-1).mkString("|.|")
      if (i != numberOfProblems) {
        problemsString += "|~|"
      }
    }

    html(
      body(onload:=s"renderQuestions(`$problemsString`, `$responseType`)")(
        script(src := "/static/trace.js")
      )
    )
  }

  initialize()
  
}