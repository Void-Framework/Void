function redirect0(url, reload, jsonData) {
    if (reload) {
        window.location.href = url;
    } else {
        history.pushState(jsonData, "", url)
    }
}

function popup0(message) {
    window.alert(message)
}

function prompt0(message, type, default0) {
    switch (type) {
        case "confirm": return window.confirm(message)
        case "prompt": return window.prompt(message, default0)
    }
}

function fetch0(url, request) {
    return fetch(request, url)
}