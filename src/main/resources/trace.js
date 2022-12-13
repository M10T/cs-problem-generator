function renderQuestions(problemsString) {
    //console.log(problemsString);
    let problems = problemsString.split("|||");
    let problems2dArr = [];
    let answersArr = []; 
    for (let i=0; i < problems.length; i++) {
        let problem = problems[i].split("|")
        problems2dArr.push(problem);
        answersArr.push('v' + problem[6]);
    }
    // console.log(problems2dArr);
    // console.log(answersArr);
    for (let i=0; i < problems2dArr.length; i++) {
        let currentForm = document.createElement("form")
        currentForm.id = `problem${i}`
        currentForm.onsubmit = function() {return answerSubmit(answersArr, i)}
        let problem = problems2dArr[i]
        currentForm.innerHTML += `<b>Problem ${i+1}:</b><br/>`;
        currentForm.innerHTML += problem[0].replace(/\n/g, "<br>").replace(/\t/g, "&emsp;"); 
        currentForm.innerHTML += "<p>What is displayed line " + problem[5] + "?</p>";
        currentForm.innerHTML += "<input type='radio' name = 'answers" + i + "' value = 'v1'>"+ problem[1] + "</input><br>";
        currentForm.innerHTML += "<input type='radio' name = 'answers" + i + "' value = 'v2'>"+ problem[2] + "</input><br>";
        currentForm.innerHTML += "<input type='radio' name = 'answers" + i + "' value = 'v3'>"+ problem[3] + "</input><br>";
        currentForm.innerHTML += "<input type='radio' name = 'answers" + i + "' value = 'v4'>"+ problem[4] + "</input><br><br>";
        currentForm.innerHTML += "<input type='submit'></input>"
        document.getElementsByTagName("body")[0].appendChild(currentForm)
    }
}

function answerSubmit(answersArr, i) {
    let windowsMessage;
    let actual = document.querySelector("input[name='answers" + i + "']:checked").value;
    if (actual == answersArr[i]) windowsMessage = "Correct"
    else windowsMessage = "Incorrect"
    window.alert(windowsMessage);
    return false;
}