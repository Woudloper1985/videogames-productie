'use strict';
import {byId, verberg} from "./util.js";
import {toonStoring} from "./storing.js";

verberg("storing");// dubbel zeker + vorige meldingen weg.

const response = await fetch("http://localhost:8080/consoles");
if (response.ok) {
    const consoles = await response.json();
    const consolesBody = byId("consolesBody");

    for (const console of consoles) {
        const tr = consolesBody.insertRow();

        //Naam + link:
        const tdName = tr.insertCell();
        tdName.classList.add("text-start");
        const link = document.createElement("a");
        link.textContent = console.name;
        link.href = "#"; // href kan # zijn, navigatie gebeurt via JS
        link.style.color = "darkgreen";
        link.style.textDecoration = "underline";
        link.onclick = () => {
            sessionStorage.setItem("consoleId", console.id);
            window.location.href = "console.html";
        };
        tdName.appendChild(link);

        const tdManufacturer = tr.insertCell();
        tdManufacturer.textContent = console.manufacturer;
        tdManufacturer.classList.add("text-start");

        tr.insertCell().textContent = console.releaseYear;
    }
} else {
    await toonStoring(response);
}