function sendRequest(el, method, url, opts = {}) {
    const targetSelector = el.getAttribute("kts-target");
    const swap = el.getAttribute("kts-swap") || "innerHTML";
    const headers = el.getAttribute("kts-headers");
    const indicator = el.getAttribute("kts-indicator");

    // Show indicator if provided
    if (indicator) {
        const indEl = document.querySelector(indicator);
        if (indEl) indEl.style.display = "block";
    }

    fetch(url, {
        method: method.toUpperCase(),
        headers: headers ? JSON.parse(headers) : {},
        body: opts.body || null
    })
        .then(resp => resp.text())
        .then(html => {
            if (targetSelector) {
                const target = document.querySelector(targetSelector);
                if (target) {
                    if (swap === "innerHTML") target.innerHTML = html;
                    else if (swap === "outerHTML") target.outerHTML = html;
                    else if (swap === "beforeend") target.insertAdjacentHTML("beforeend", html);
                    else if (swap === "afterbegin") target.insertAdjacentHTML("afterbegin", html);
                    else target.innerHTML = html; // fallback
                }
            }
        })
        .finally(() => {
            // Hide indicator if provided
            if (indicator) {
                const indEl = document.querySelector(indicator);
                if (indEl) indEl.style.display = "none";
            }
        });
}

function processElement(el) {
    for (let attr of el.attributes) {
        if (attr.name.startsWith("kts-")) {
            // request attributes like kts-get, kts-post, etc.
            if (["kts-get", "kts-post", "kts-put", "kts-delete"].includes(attr.name)) {
                const method = attr.name.replace("kts-", "");
                const url = attr.value;

                // default trigger is click
                const trigger = el.getAttribute("kts-trigger") || "click";

                if (trigger === "load") {
                    // fire immediately on page load
                    sendRequest(el, method, url);
                } else {
                    // bind to event
                    el.addEventListener(trigger, () => {
                        // confirm support
                        const confirmMsg = el.getAttribute("kts-confirm");
                        if (confirmMsg && !window.confirm(confirmMsg)) return;

                        sendRequest(el, method, url);
                    });
                }
            }
        }
    }
}

document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("*").forEach(el => {
        for (let attr of el.attributes) {
            if (attr.name.startsWith("kts-")) {
                processElement(el);
                break; // only need to process once
            }
        }
    });
});
