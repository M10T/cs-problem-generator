function answerSubmit(correct) {
    let actual = document.querySelector('input[name="answers"]:checked').value;
    if (actual == correct) window.alert("Correct!")
    else window.alert("Incorrect!")
    return false;
}