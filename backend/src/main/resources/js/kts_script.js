function sendRequest(el, method, url, opts = {}) {
    const targetSelector = el.getAttribute("kts-target");
    const swap = el.getAttribute("kts-swap") || "innerHTML";
    const headersAttr = el.getAttribute("kts-headers");
    const indicator = el.getAttribute("kts-indicator");

    const defaultHeaders = {
        "KTS-Request": "true",
        "KTS-Method": method.toUpperCase(),
        "KTS-Trigger": el.id || "",
        "KTS-Target": targetSelector || "",
        "KTS-Confirm": el.getAttribute("kts-confirm") || "",
        "KTS-Route": window.location.pathname,
    };

    // Merge user-defined headers
    let customHeaders = {};
    if (headersAttr) {
        try {
            customHeaders = JSON.parse(headersAttr);
        } catch (e) {
            console.warn("Invalid kts-headers JSON", e);
        }
    }

    const headers = { ...defaultHeaders, ...customHeaders };

    // Show indicator if specified
    if (indicator) {
        const indEl = document.querySelector(indicator);
        if (indEl) indEl.style.display = "block";
    }

    fetch(url, {
        method: method.toUpperCase(),
        headers: headers,
        body: opts.body || null
    })
        .then(resp => resp.text())
        .then(html => {
            if (targetSelector) {
                const target = document.querySelector(targetSelector);
                if (target) {
                    switch (swap) {
                        case "innerHTML":
                            target.innerHTML = html;
                            break;
                        case "outerHTML":
                            target.outerHTML = html;
                            break;
                        case "beforeend":
                        case "afterbegin":
                            target.insertAdjacentHTML(swap, html);
                            break;
                        default:
                            target.innerHTML = html;
                    }
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

                const trigger = el.getAttribute("kts-trigger") || "click";

                if (trigger === "load") {
                    sendRequest(el, method, url);
                } else {
                    el.addEventListener(trigger, () => {
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
        if ([...el.attributes].some(attr => attr.name.startsWith("kts-"))) {
            processElement(el);
        }
    });
});
