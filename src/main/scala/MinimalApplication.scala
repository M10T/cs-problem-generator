package app

import model._
import scalatags.Text.all._


object MinimalApplication extends cask.MainRoutes{
  @cask.get("/")
  def welcome() = {
        doctype("html")(
    html(
      body(
        div(b("Welcome to CS Problem Generator!"),
        br()),
        form(action:="/form", method:="POST")(
          input(attr("type"):="checkbox", attr("id"):="problemTypeOne"),
          label(attr("for"):="problemTypeOne")("Problem Type One: Tracing"),
          br(),
          input(attr("type"):="checkbox", attr("id"):="problemTypeTwo"),
          label(attr("for"):="problemTypeTwo")("Problem Type Two: "),
          br(),
          input(attr("type"):="submit", attr("id"):="submit"),
        )
      )
    ))
  }

  @cask.post("/do-thing")
  def doThing(request: cask.Request) = {
    request.text().reverse
  }

  @cask.get("/random_code") 
  def random_code() = {
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
        p(b("Displayed: "),displayed),
        p(b("Data: "),data.toString)
      )
    )
  }

  @cask.postForm("/form")
  def formEndpoint(value1: cask.FormValue) = {
    "OK " + value1
  }

  initialize()
}