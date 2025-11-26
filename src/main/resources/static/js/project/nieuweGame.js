'use strict';
import {byId, toon, verberg} from "./util.js";

// bij laden genres en consoles ophalen
window.onload = async () => {
    await vulGenres();
    await vulConsoles();
};

async function vulGenres() {
    const response = await fetch("http://localhost:8080/games/genres");
    if (response.ok) {
        const genres = await response.json();
        const genreSelect = byId("genre");
        for (const genre of genres) {
            const option = document.createElement("option");
            option.value = genre;
            option.textContent = genre;
            genreSelect.appendChild(option);
        }
    } else {
        toon("storing");
    }
}

async function vulConsoles() {
    const response = await fetch("http://localhost:8080/consoles");
    if (response.ok) {
        const consoles = await response.json();
        const container = byId("consolesContainer");
        for (const console of consoles) {
            const label = document.createElement("label");
            label.className = "d-block"; // netjes onder elkaar

            const checkbox = document.createElement("input");
            checkbox.type = "checkbox";
            checkbox.name = "console";
            checkbox.value = console.id;

            label.appendChild(checkbox);
            label.appendChild(document.createTextNode(` ${console.name}`));
            container.appendChild(label);
        }
    } else {
        toon("storing");
    }
}

byId("toevoegen").onclick = async (e) => {
    e.preventDefault();

    // alle foutmeldingen verbergen
    verberg(
        "titelFout",
        "developerFout",
        "genreFout",
        "consolesFout",
        "releasedatumFout",
        "gameBestaatAlFout",
        "consoleNietGevondenFout",
        "storing"
    );

    const titelInput = byId("titel");
    const developerInput = byId("developer");
    const releasedatumInput = byId("releasedatum");
    const genreInput = byId("genre");

    // trimmen
    titelInput.value = titelInput.value.trim();
    developerInput.value = developerInput.value.trim();

    let aantalFouten = 0;

    // frontend checks
    if (titelInput.value === "") { toon("titelFout"); aantalFouten++; }
    if (developerInput.value === "") { toon("developerFout"); aantalFouten++; }
    if (genreInput.value === "") { toon("genreFout"); aantalFouten++; }

// datum check frontend
    const vandaag = new Date();
    const minDatum = new Date("1970-01-01");
    const ingevoerdeDatum = new Date(releasedatumInput.value);

    if (releasedatumInput.value === "" || ingevoerdeDatum > vandaag || ingevoerdeDatum < minDatum) {
        toon("releasedatumFout");
        aantalFouten++;
    }

    // consoles check frontend
    const geselecteerdeConsoles = Array.from(document.querySelectorAll('input[name="console"]:checked'))
        .map(cb => Number(cb.value));
    if (geselecteerdeConsoles.length === 0) { toon("consolesFout"); aantalFouten++; }

    // enkel toevoegen als geen frontend fouten
    if (aantalFouten === 0) {
        const game = {
            title: titelInput.value,
            developer: developerInput.value,
            releaseDate: releasedatumInput.value,
            genre: genreInput.value,
            consoleIds: geselecteerdeConsoles
        };

        await voegToe(game);
    }
};

async function voegToe(game) {
    const response = await fetch("http://localhost:8080/games", {
        method: "POST",
        headers: {'Content-Type': "application/json"},
        body: JSON.stringify(game)
    });

    if (response.ok) {
        window.location = "games.html";
    } else if (response.status === 400) {
        toon("releasedatumFout"); // backend valideert definitief
    } else if (response.status === 404) {
        toon("consoleNietGevondenFout");
    } else if (response.status === 409) {
        toon("gameBestaatAlFout");
    } else {
        toon("storing");
    }
}