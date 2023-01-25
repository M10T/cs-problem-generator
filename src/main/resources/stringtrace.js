function answerSubmit(i) {
    let windowsMessage;
    windowsMessage = document.querySelector("input[name='answers" + i + "']:checked").value;
    window.alert(windowsMessage);
    return false; 
}