package app

import model._

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
    println(JavaTranslator.translateModel(model))
    val executedContext = CodeExecutor.executeModel(model)
    println("Displayed: " + executedContext.getDisplayed)
    println("Data: " + executedContext.getData())
  }

  initialize()
}
