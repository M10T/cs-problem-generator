function renderQuestions(problemsString, responseType) {
    console.log(responseType);

    let problems = problemsString.split("|||");
    let problems2dArr = [];
    let answersArr = []; 
    for (let i=0; i < problems.length; i++) {
        let problem = problems[i].split("|")
        problems2dArr.push(problem);
        answersArr.push('v' + problem[6]);
    }
  
    for (let i=0; i < problems2dArr.length; i++) {
        let currentForm = document.createElement("form")
        currentForm.id = `problem${i}`
        let problem = problems2dArr[i]
        currentForm.innerHTML += `<b>Problem ${i+1}:</b><br/>`;
        currentForm.innerHTML += problem[0].replace(/\n/g, "<br>").replace(/\t/g, "&emsp;"); 
        currentForm.innerHTML += "<p>What is displayed line " + problem[5] + "?</p>";
        if (responseType == "multipleChoice") {
            currentForm.innerHTML += "<input type='radio' name='answers" + i + "' value = 'v1'>"+ problem[1] + "</input><br>";
            currentForm.innerHTML += "<input type='radio' name='answers" + i + "' value = 'v2'>"+ problem[2] + "</input><br>";
            currentForm.innerHTML += "<input type='radio' name='answers" + i + "' value = 'v3'>"+ problem[3] + "</input><br>";
            currentForm.innerHTML += "<input type='radio' name='answers" + i + "' value = 'v4'>"+ problem[4] + "</input><br><br>";
            currentForm.onsubmit = function() {return answerSubmit(answersArr, i, responseType)}
        } else if (responseType == "freeResponse") {
            currentForm.innerHTML += "<input type='text' name='answers" + i + "'></input><br>";
            currentForm.onsubmit = function() {return answerSubmit([problem[problem[6]]], i, responseType)}
        }
        currentForm.innerHTML += "<input type='submit'></input>"
        document.getElementsByTagName("body")[0].appendChild(currentForm)
    }
}

function answerSubmit(answersArr, i, responseType) {
    let windowsMessage;
    let actual;
    if (responseType == "multipleChoice") {
        actual = document.querySelector("input[name='answers" + i + "']:checked").value;
        if (actual == answersArr[i]) windowsMessage = "Correct"
        else windowsMessage = "Incorrect"
        window.alert(windowsMessage);
    } else if (responseType == "freeResponse") {
        actual = document.querySelector("input[name='answers" + i + "']").value;
        if (actual == answersArr[0]) windowsMessage = "Correct"
        else windowsMessage = "Incorrect"
        window.alert(windowsMessage);
    }
    return false; 
}