'use strict';
import {setText, toon} from "./util.js";

export async function toonStoring(response) {
    if (response.status === 404 || response.status === 409) {
        const body = await response.json();
        setText("storing", body.message);
    } else {
        setText("storing", "STORING");
    }
    toon("storing");
}