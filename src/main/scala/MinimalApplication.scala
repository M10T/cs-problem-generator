package app

import model._
import scalatags.Text.all._
import scala.io.Source

import scala.collection.mutable.ArrayBuffer

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
          input(attr("type"):="number", attr("id"):="numberOfProblems", attr("name"):="numberOfProblems", attr("value"):=""),
          input(attr("type"):="submit", attr("id"):="submit"),
        )
      )
    ))
  }

  @cask.get("/problemTypeSelector")
  def problemTypeSelector(problemType: String, numberOfProblems: Int) = problemType match
    case "randomCode" => cask.Redirect("/randomCode")
    case "trace" => cask.Redirect("/trace/")
    case _ => cask.Abort(404)
  

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
          translated.split("\n").map(x=>p(x))),
        br(),
        p(b("Displayed: "),displayed.toString),
        p(b("Data: "),data.toString)
      )
    )
  }

  @cask.staticFiles("/static")
  def staticFileRoutes() = "/src/main/resources"

  @cask.get("/trace")
  def trace(numberOfProblems: Int) = {
    /*
    val r = scala.util.Random
    val problems = new ArrayBuffer[Any]() // scala.collection.mutable.Map[String, Any]()

    for (i <- 0 to numberOfProblems){
      val model = JavaTranslator.randomGenerate()
      val translated = JavaTranslator.translateModel(model)
      val executedContext = CodeExecutor.executeModel(model)
      val displayed = executedContext.getDisplayed
      val data = executedContext.getData()
      val answerIndex = r.nextInt(4) + 1
      val answer = if displayed.isEmpty then "N/A" else s"${displayed(0)}"

      var answerChoices = new ArrayBuffer[String]()

      for(i <- 1 to 4){
        if (i == answerIndex) {
          answerChoices += answer
        } else {
          answerChoices += JavaString.randomGenerate()
        }
      }

      val problem = scala.collection.mutable.Map[String, Any]()

      problem += ("problemStatement" -> translated)
      problem += ("answerChoices" -> answerChoices)

      problems += problem
    } */

    val r = scala.util.Random
    var problems = Array.ofDim[String](numberOfProblems, 5) // new Array[String](numberOfProblems) // scala.collection.mutable.Map[String, Any]()

    for (i <- 1 to numberOfProblems){
      val model = JavaTranslator.randomGenerate()
      val translated = JavaTranslator.translateModel(model)
      val executedContext = CodeExecutor.executeModel(model)
      val displayed = executedContext.getDisplayed
      val data = executedContext.getData()
      val answerIndex = r.nextInt(4) + 1
      val answer = if displayed.isEmpty then "N/A" else s"${displayed(0)}"

      // var answerChoices = new Array[String](4)
      // val problem = new Array[String](5)

      problems(i-1)(0) = translated

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
      problemsString += problems(i-1).mkString("|")
      if (i != numberOfProblems) {
        problemsString += "|"
      }
    }

    html(
      body(onload:="renderQuestions('" + problemsString + "')")(
        div(numberOfProblems),
        br(),
        //div(problemsString), 
        /*
        div(b("Code:"),
          translated.split("\n").map(x=>p(x))),
        br(),
        form(id:="answers", onsubmit:="return answerSubmit('v" + answerIndex + "')",
          p(s"What is displayed line #${line}?"),
          input(tpe:="radio", name:="answers", value:="v1", options(0)),
          br(),
          input(tpe:="radio", name:="answers", value:="v2", answerChoices(1)),
          br(),
          input(tpe:="radio", name:="answers", value:="v3", answerChoices(2)),
          br(),
          input(tpe:="radio", name:="answers", value:="v4", answerChoices(3)),
          br(),
          input(tpe:="submit")
        ), */
        script(src := "/static/trace.js")
      )
    )
  }

  initialize()
  
}