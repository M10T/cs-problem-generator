function renderQuestions(problems) {
    console.log("hiiii");
    console.log(problems);
    /*
    document.getElementsByTagName("body")[0].innerHTML += "Code:";
    for (let i=0; i < problems.size; i++) {
        document.getElementsByTagName("body")[0].innerHTML += "Code:";
    }*/
}

function answerSubmit(correct) {
    let actual = document.querySelector('input[name="answers"]:checked').value;
    if (actual == correct) window.alert("Correct!")
    else window.alert("Incorrect!")
    return false;
}