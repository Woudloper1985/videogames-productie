'use strict';
import {byId, verberg} from "./util.js";
import {toonStoring} from "./storing.js";

verberg("storing");// dubbel zeker + vorige meldingen weg.

const gameId = sessionStorage.getItem("gameId");

const response = await fetch(`http://localhost:8080/games/${gameId}`);
if (response.ok) {
    const gameData = await response.json();

    // Game-info bovenaan
    byId("gameTitel").textContent = gameData.title;
    byId("gameInfo").textContent = `${gameData.developer} (${gameData.releaseDate}) - ${gameData.genre}`;

    // Consoles-tabel vullen
    const tbody = byId("consolesBody");
    tbody.innerHTML = "";

    gameData.consoles.forEach(console => {
        const tr = tbody.insertRow();
        tr.insertCell().textContent = console.name;
        tr.insertCell().textContent = console.manufacturer;
        tr.insertCell().textContent = console.releaseYear;

        const tdVerwijder = tr.insertCell();
        tdVerwijder.classList.add("text-center");
        const button = document.createElement("button");
        button.textContent = "Verwijder";
        button.classList.add("btn", "btn-danger");
        button.onclick = async () => {
            const resp = await fetch(
                `http://localhost:8080/consoles/${console.id}/removeGame/${gameId}`,
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