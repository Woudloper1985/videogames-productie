'use strict';
import {byId, toon, verberg} from "./util.js";

byId("toevoegen").onclick = async (e) => {
    e.preventDefault(); // voorkomt herladen
    verberg("naamFout", "makerFout", "releasejaarFout", "consoleBestaatAlFout", "storing");

    const naamInput = byId("naam");
    const makerInput = byId("maker");
    const releasejaarInput = byId("releasejaar");

    // trimmen van spaties
    naamInput.value = naamInput.value.trim();
    makerInput.value = makerInput.value.trim();

    let aantalFouten = 0;

    // frontend-validatie
    if (!naamInput.checkValidity() || naamInput.value === "") {
        toon("naamFout");
        aantalFouten++;
    }
    if (!makerInput.checkValidity() || makerInput.value === "") {
        toon("makerFout");
        aantalFouten++;
    }

    const huidigJaar = new Date().getFullYear();
    const ingevoerdJaar = Number(releasejaarInput.value);

    if (!releasejaarInput.checkValidity() || !releasejaarInput.value || ingevoerdJaar > huidigJaar) {
        toon("releasejaarFout");
        aantalFouten++;
    }

    if (aantalFouten === 0) {
        // bevestiging vóór toevoegen:
        if (!confirm("Ben je zeker? Kloppen alle gegevens?")) {
            return; // stopt hier als gebruiker annuleert.
        }

        const console = {
            name: naamInput.value,
            manufacturer: makerInput.value,
            releaseYear: ingevoerdJaar
        };
        await voegToe(console);
    }
};

async function voegToe(console) {
    const response = await fetch("http://localhost:8080/consoles", {
        method: "POST",
        headers: {'Content-Type': "application/json"},
        body: JSON.stringify(console)
    });

    if (response.ok) {
        window.location = "consoles.html";
    } else if (response.status === 409) {
        toon("consoleBestaatAlFout");
    } else if (response.status === 400) {
        toon("releasejaarFout");
    } else {
        toon("storing");
    }
}
