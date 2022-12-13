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
    document.getElementsByTagName("body")[0].innerHTML += `<form id='problems' onsubmit = "return answerSubmit(` + answersArr + `)"></form>`;
    for (let i=0; i < problems2dArr.length; i++) {
        let problem = problems2dArr[i]
        // console.log(problem);
        document.getElementById(`problems`).innerHTML += "Code:" + "<br>";
        document.getElementById(`problems`).innerHTML += problem[0].replace(/\n/g, "<br>").replace(/\t/g, "&emsp;"); 
        document.getElementById(`problems`).innerHTML += "<p>What is displayed line " + problem[5] + "?</p>";
        document.getElementById(`problems`).innerHTML += "<input type='radio' name = 'answers" + i + "' value = 'v1'>"+ problem[1] + "</input><br>";
        document.getElementById(`problems`).innerHTML += "<input type='radio' name = 'answers" + i + "' value = 'v2'>"+ problem[2] + "</input><br>";
        document.getElementById(`problems`).innerHTML += "<input type='radio' name = 'answers" + i + "' value = 'v3'>"+ problem[3] + "</input><br>";
        document.getElementById(`problems`).innerHTML += "<input type='radio' name = 'answers" + i + "' value = 'v4'>"+ problem[4] + "</input><br><br>";
    }
    document.getElementById(`problems`).innerHTML += "<input type='submit'></input><br>";
}

function answerSubmit(answersArr) {
    let windowsMessage = "";
    for (let i=0; i < answersArr.length; i++){
        let actual = document.querySelector("input[name='answers" + i + "']:checked").value;
        if (actual == answersArr[i]) windowsMessage += i + ". Correct"
        else windowsMessage += i + ". Incorrect"
    }
    console.log(windowsMessage);
    window.alert(windowsMessage);
    return false; 
}