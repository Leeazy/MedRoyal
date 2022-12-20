$(document).ready(function() {
    // Year prefix
    $("#yearPrefix").text(plural($("#doctorExperience").text()));

    // ResumeParts render
    const edjsParser = edjsHTML();
    $("#aboutDoctorDiv").html(edjsParser.parse(JSON.parse(aboutDoctor)));
    $("#educationDiv").html(edjsParser.parse(JSON.parse(education)));
    $("#workPlacesDiv").html(edjsParser.parse(JSON.parse(workPlaces)));

});