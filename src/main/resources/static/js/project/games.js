'use strict';
import {byId, verberg} from "./util.js";
import {toonStoring} from "./storing.js";

verberg("storing");

// --- Elementen ---
const genreFilter = byId("genreFilter");
const titleFilter = byId("titleFilter");
const gamesBody = byId("gamesBody");
const gamesTitel = byId("gamesTitel");

// --- Initialisatie: ophalen van totaal aantal, genres en games ---
await haalAantalGamesOp();
await haalGenresOp();
await toonGames();

// --- Eventlisteners ---
genreFilter.addEventListener("change", toonGames);

let zoekTimer;
titleFilter.addEventListener("input", () => {
    clearTimeout(zoekTimer);
    zoekTimer = setTimeout(toonGames, 400);
});

// -------------------- Functies (zelfde volgorde als aanroep) --------------------

async function haalAantalGamesOp() {
    const response = await fetch("http://localhost:8080/games/aantal");
    if (response.ok) {
        const aantal = await response.text();
        gamesTitel.textContent = `Games (${aantal})`;
    } else {
        await toonStoring(response)
    }
}

async function haalGenresOp() {
    const response = await fetch("http://localhost:8080/games/genres");
    if (response.ok) {
        const genres = await response.json();
        genres.forEach(genre => {
            const option = document.createElement("option");
            option.value = genre;
            option.textContent = genre;
            genreFilter.appendChild(option);
        });
    } else {
        await toonStoring(response);
    }
}

async function toonGames() {
    gamesBody.innerHTML = "";

    const genre = genreFilter.value;
    const title = titleFilter.value.trim();

    let url = "http://localhost:8080/games";
    if (title) {
        url += `?title=${encodeURIComponent(title)}`;
    } else if (genre) {
        url += `/genre/${encodeURIComponent(genre)}`;
    }

    const response = await fetch(url);
    if (response.ok) {
        const games = await response.json();
        for (const game of games) {
            const tr = gamesBody.insertRow();

            // Titel + link
            const tdTitle = tr.insertCell();
            tdTitle.classList.add("text-start");
            const link = document.createElement("a");
            link.textContent = game.title;
            link.href = "#";
            link.style.color = "blue";
            link.style.textDecoration = "underline";
            link.onclick = () => {
                sessionStorage.setItem("gameId", game.id);
                window.location.href = "game.html";
            };
            tdTitle.appendChild(link);

            // Developer
            const tdDeveloper = tr.insertCell();
            tdDeveloper.textContent = game.developer;
            tdDeveloper.classList.add("text-start");

            // ReleaseDate
            const tdRelease = tr.insertCell();
            tdRelease.textContent = game.releaseDate;

            // Genre
            const tdGenre = tr.insertCell();
            tdGenre.textContent = game.genre;
            tdGenre.classList.add("text-start");

            // Verwijder-knop
            const tdVerwijder = tr.insertCell();
            tdVerwijder.classList.add("text-center");
            const button = document.createElement("button");
            button.textContent = "Verwijder";
            button.classList.add("btn", "btn-danger");
            button.onclick = async () => {
                const resp = await fetch(`http://localhost:8080/games/${game.id}`, {method: "DELETE"});
                if (resp.ok) {
                    tr.remove();                 // verwijder de rij
                    await haalAantalGamesOp();   // update het aantal games
                } else {
                    await toonStoring(resp);
                }
            };
            tdVerwijder.appendChild(button);
        }
    } else {
        await toonStoring(response);
    }
}