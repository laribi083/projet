// toggle sidebar (FIXED)
document.getElementById("toggleBtn").onclick = () => {
    document.getElementById("sidebar").classList.toggle("collapsed");
};

// animation numbers
function animate(id, end) {
    let el = document.getElementById(id);
    let i = 0;

    let interval = setInterval(() => {
        i += Math.ceil(end / 50);
        if (i >= end) {
            i = end;
            clearInterval(interval);
        }
        el.innerText = i;
    }, 20);
}

animate("users", 1234);
animate("courses", 456);
animate("pending", 23);
animate("validated", 433);