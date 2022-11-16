import play.api.data._
import play.api.data.Forms._

import play.api.data.validation.Constraints._

case class ProblemForm(problemTypeOneTracing: Boolean, numberOfProblemTypeOne: Int)

object ProblemForm {
  val form: Form[ProblemForm] = Form(
    mapping(
        "problemTypeOneTracing" -> boolean,
        "numberOfProblemTypeOne" -> number
    )(ProblemForm.apply)(ProblemForm.unapply)
)   
}

