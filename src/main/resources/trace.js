function renderQuestions(problemsString) {
    //console.log(problemsString);
    let problems = problemsString.split("|||");
    //console.log(problems);
    for (let i=0; i < problems.length; i++) {
        let problem = problems[i].split("|")
        console.log(problem);
        document.getElementsByTagName("body")[0].innerHTML += "Code:" + "<br>";
        document.getElementsByTagName("body")[0].innerHTML += problem[0].replace(/\n/g, "<br>").replace(/\t/g, "&emsp;"); 
        document.getElementsByTagName("body")[0].innerHTML += `<form id='answers${i}' onsubmit = "return answerSubmit('v${problem[6]}', ${i})"></form>`;
        document.getElementById(`answers${i}`).innerHTML += "<p>What is displayed line " + problem[5] + "?</p>";
        document.getElementById(`answers${i}`).innerHTML += "<input type='radio' name = 'answers" + i + "' value = 'v1'>"+ problem[1] + "</input><br>";
        document.getElementById(`answers${i}`).innerHTML += "<input type='radio' name = 'answers" + i + "' value = 'v2'>"+ problem[2] + "</input><br>";
        document.getElementById(`answers${i}`).innerHTML += "<input type='radio' name = 'answers" + i + "' value = 'v3'>"+ problem[3] + "</input><br>";
        document.getElementById(`answers${i}`).innerHTML += "<input type='radio' name = 'answers" + i + "' value = 'v4'>"+ problem[4] + "</input><br>";
        document.getElementById(`answers${i}`).innerHTML += "<input type='submit'></input><br>";
    }
}

function answerSubmit(correct, problemNumber) {
    console.log("jdfkasdjklfjasdf");
    let actual = document.querySelector("input[name='answers" + problemNumber + "']:checked").value;
    if (actual == correct) window.alert("Correct!")
    else window.alert("Incorrect!")
    return false;
}