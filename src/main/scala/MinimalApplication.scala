package app

import model._
import scalatags.Text.all._
import scala.io.Source

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
          input(attr("type"):="submit", attr("id"):="submit"),
        )
      )
    ))
  }

  @cask.get("/problemTypeSelector")
  def problemTypeSelector(problemType: String) = problemType match
    case "randomCode" => cask.Redirect("/randomCode")
    case "trace" => cask.Redirect("/trace")
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
          translated.split(";").map(x=>p(x+';'))),
        br(),
        p(b("Displayed: "),displayed.toString),
        p(b("Data: "),data.toString)
      )
    )
  }

  @cask.staticFiles("/static")
  def staticFileRoutes() = "/src/main/resources"

  @cask.get("/trace")
  def trace() = {
    val model = JavaTranslator.randomGenerate()
    val translated = JavaTranslator.translateModel(model)
    val executedContext = CodeExecutor.executeModel(model)
    val displayed = executedContext.getDisplayed
    val data = executedContext.getData()
    val firstDisplayed = if displayed.isEmpty then "N/A" else s"${displayed(0)}"
    html(
      body(
        div(b("Code:"),
          translated.split(";").map(x=>p(x+';'))),
        br(),
        form(id:="answers",onsubmit:="return answerSubmit('v1')",
          p("What is the first displayed line?"),
          input(tpe:="radio", name:="answers", value:="v1", firstDisplayed),
          br(),
          input(tpe:="radio", name:="answers", value:="v2", JavaString.randomGenerate()),
          br(),
          input(tpe:="radio", name:="answers", value:="v3", JavaString.randomGenerate()),
          br(),
          input(tpe:="radio", name:="answers", value:="v4", JavaString.randomGenerate()),
          br(),
          input(tpe:="submit")
        ),
        script(src := "/static/trace.js")
      )
    )
  }

  initialize()
  
}