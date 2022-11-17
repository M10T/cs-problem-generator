package app

import model._
import scalatags.Text.all._


object MinimalApplication extends cask.MainRoutes{
  @cask.get("/")
  def hello() = {
    "Hello World!"
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
  initialize()
}

