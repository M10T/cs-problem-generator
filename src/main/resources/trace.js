function renderQuestions(problemsString) {
    //console.log(problemsString);
    let problems = problemsString.split("|||");
    //console.log(problems);
    for (let i=0; i < problems.length; i++) {
        let problem = problems[i].split("|")
        console.log(problem);
        document.getElementsByTagName("body")[0].innerHTML += "Code:" + "<br>";
        document.getElementsByTagName("body")[0].innerHTML += problem[0].replace(/\n/g, "<br>").replace(/\t/g, "&emsp;"); 
        document.getElementsByTagName("body")[0].innerHTML += "<form id='answers" + i + "' onsubmit = 'answerSubmit('v" + problem[5] + "', " + i + ")>";
        document.getElementsByTagName("body")[0].innerHTML += "<p>What is displayed line 1?</p>";
        document.getElementsByTagName("body")[0].innerHTML += "<input type='radio' name = 'answers" + i + "' value = 'v1'>"+ problem[1] + "</input><br>";
        document.getElementsByTagName("body")[0].innerHTML += "<input type='radio' name = 'answers" + i + "' value = 'v2'>"+ problem[2] + "</input><br>";
        document.getElementsByTagName("body")[0].innerHTML += "<input type='radio' name = 'answers" + i + "' value = 'v3'>"+ problem[3] + "</input><br>";
        document.getElementsByTagName("body")[0].innerHTML += "<input type='radio' name = 'answers" + i + "' value = 'v4'>"+ problem[4] + "</input><br>";
        document.getElementsByTagName("body")[0].innerHTML += "<input type='submit'></input><br></form>";
    }
}

function answerSubmit(correct, problemNumber) {
    let actual = document.querySelector("input[name='answers" + problemNumber + "']:checked").value;
    if (actual == correct) window.alert("Correct!")
    else window.alert("Incorrect!")
    return false;
}