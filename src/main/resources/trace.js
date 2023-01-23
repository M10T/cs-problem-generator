
function renderQuestions(problemsString, responseType) {
    console.log(responseType);

    let responseTypesBank = ["multipleChoice", "freeResponse"]

    let problems = problemsString.split("|||");
    let problems2dArr = [];
    let answersArr = []; 
    for (let i=0; i < problems.length; i++) {
        let problem = problems[i].split("|")
        problems2dArr.push(problem);
        answersArr.push('v' + problem[6]);
    }
  
    for (let i=0; i < problems2dArr.length; i++) {
        let currentResponseType; 
        if (responseType == "mixedResponseTypes") {
            currentResponseType = responseTypesBank[Math.floor(Math.random() * 2)];
        } else {
            currentResponseType = responseType;
        }
        let currentForm = document.createElement("form")
        currentForm.id = `problem${i}`
        let problem = problems2dArr[i]

        let lineNumberCounter = 1;

        currentForm.innerHTML += `<b>Problem ${i+1}:</b><br/>`;

        problem[0] = problem[0].split("\n")

        for (let j = 0; j < problem[0].length; j++) {
            currentForm.innerHTML += "<i>" + lineNumberCounter + "</i>&emsp;" + problem[0][j].replace(/\t/g, "&emsp;") + "<br>"; 
            lineNumberCounter ++;
        }

        //currentForm.innerHTML += problem[0].replace(/\n/g, replacer(lineNumberCounter)).replace(/\t/g, "&emsp;"); 
        //currentForm.innerHTML += problem[0].replace(/\n/g, "<br><i>1. </i>").replace(/\t/g, "&emsp;"); 
        currentForm.innerHTML += "<p>What is displayed line " + problem[5] + "?</p>";
        if (currentResponseType == "multipleChoice") {
            currentForm.innerHTML += "<input type='radio' name='answers" + i + "' value = 'v1'>"+ problem[1] + "</input><br>";
            currentForm.innerHTML += "<input type='radio' name='answers" + i + "' value = 'v2'>"+ problem[2] + "</input><br>";
            currentForm.innerHTML += "<input type='radio' name='answers" + i + "' value = 'v3'>"+ problem[3] + "</input><br>";
            currentForm.innerHTML += "<input type='radio' name='answers" + i + "' value = 'v4'>"+ problem[4] + "</input><br><br>";
            currentForm.onsubmit = function() {return answerSubmit(answersArr, i, currentResponseType)}
        } else if (currentResponseType == "freeResponse") {
            currentForm.innerHTML += "<input type='text' name='answers" + i + "'></input><br>";
            currentForm.onsubmit = function() {return answerSubmit([problem[problem[6]]], i, currentResponseType)}
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

function replacer(match, p1, offset, string, groups, lineNumberCounter) {
    let replacement = "<br><i>" + lineNumberCounter + "</i>";
    lineNumberCounter++
    return replacement;
}