'use strict';
import {byId, toon, verberg} from "./util.js";
import {toonStoring} from "./storing.js";

verberg("storing");

const gameId = sessionStorage.getItem("gameId"); // gameId uit sessionStorage

// Terug-knop
byId("terugKnop").onclick = () => {
    window.location = "game.html";
};

// Item info
async function laadGameInfo() {
    const resp = await fetch(`http://localhost:8080/games/${gameId}`);
    if (resp.ok) {
        const game = await resp.json();
        byId("itemTitel").textContent = game.title;
        byId("itemDetails").textContent = `${game.developer} (${game.releaseDate}) - ${game.genre}`;
        await laadBeschikbareConsoles(game.consoles);
    } else {
        await toonStoring(resp);
    }
}

// Alleen consoles tonen die nog niet aan de game zijn toegevoegd
async function laadBeschikbareConsoles(ingeslotenConsoles) {
    const resp = await fetch("http://localhost:8080/consoles");
    if (resp.ok) {
        const alleConsoles = await resp.json();
        const container = byId("selectDoel");
        container.innerHTML = "";

        const bestaandeIds = new Set(ingeslotenConsoles.map(c => c.id));

        const beschikbareConsoles = alleConsoles.filter(c => !bestaandeIds.has(c.id));

        if (beschikbareConsoles.length === 0) {
            const p = document.createElement("p");
            p.textContent = "Geen beschikbare consoles om toe te voegen.";
            container.appendChild(p);
            byId("toevoegenBtn").disabled = true;
            return;
        }

        // radio buttons maken
        beschikbareConsoles.forEach(console => {
            const label = document.createElement("label");
            label.className = "d-block";

            const input = document.createElement("input");
            input.type = "radio";
            input.name = "console";
            input.value = console.id;

            label.appendChild(input);
            label.appendChild(document.createTextNode(` ${console.name} (${console.manufacturer})`));
            container.appendChild(label);
        });
    } else {
        await toonStoring(resp);
    }
}

// Toevoegen knop
byId("toevoegenBtn").onclick = async () => {
    const geselecteerde = document.querySelector('input[name="console"]:checked');
    if (!geselecteerde) {
        toon("storing");
        byId("storing").textContent = "Selecteer een console.";
        byId("storing").hidden = false;
        return;
    }

    const consoleId = geselecteerde.value;

    const resp = await fetch(`http://localhost:8080/games/${gameId}/addConsole/${consoleId}`, {
        method: "PUT"
    });

    if (resp.ok) {
        window.location = "game.html"; // terug naar game-pagina
    } else {
        await toonStoring(resp);
    }
};

await laadGameInfo();
