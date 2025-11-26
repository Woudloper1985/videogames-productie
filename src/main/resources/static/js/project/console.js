'use strict';
import {byId, verberg} from "./util.js";
import {toonStoring} from "./storing.js";

verberg("storing");// dubbel zeker + vorige meldingen weg.

const consoleId = sessionStorage.getItem("consoleId");

const response = await fetch(`http://localhost:8080/consoles/${consoleId}`);
if (response.ok) {
    const consoleData = await response.json();

    // Console-info bovenaan
    byId("consoleNaam").textContent = consoleData.name;
    byId("consoleInfo").textContent = `${consoleData.manufacturer} - ${consoleData.releaseYear}`;

    // Games-tabel vullen
    const tbody = byId("gamesBody");
    tbody.innerHTML = "";

    consoleData.games.forEach(game => {
        const tr = tbody.insertRow();
        tr.insertCell().textContent = game.title;
        tr.insertCell().textContent = game.developer;
        tr.insertCell().textContent = game.releaseDate;
        tr.insertCell().textContent = game.genre;

        const tdVerwijder = tr.insertCell();
        tdVerwijder.classList.add("text-center");
        const button = document.createElement("button");
        button.textContent = "Verwijder";
        button.classList.add("btn", "btn-danger");
        button.onclick = async () => {
            const resp = await fetch(
                `http://localhost:8080/consoles/${consoleId}/removeGame/${game.id}`,
                {method: "DELETE"});
            if (resp.ok) {
                tr.remove();
            } else {
                await toonStoring(resp);
            }
        };
        tdVerwijder.appendChild(button);
    });
} else {
    await toonStoring(response);
}