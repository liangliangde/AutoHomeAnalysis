

if (!String.prototype.format) {
    String.prototype.format = function () {
        var args = arguments;
        return this.replace(/{(\d+)}/g, function (match, number) {
            return typeof args[number] != 'undefined'
                ? args[number]
                : match
                ;
        });
    };
}

$(function () {

    /* UNKNOWN ERROR ON LOADING CSS with <link> tag , reloading jquer-ui.css with ajax*/
    $.ajax({
        url: "css/jquery-ui-1.10.2.custom.css",
        success: function (data) {
            $("head").append("<style>" + data + "</style>");
            //loading complete code here
        },
        error: function (xhr, msg, x) {
            alert(xhr);
        }
    });

    var options = {
        File: "data/users.csv",
        EntryLimit: 5000,
        TableSelectedEntitiesID: "tabSelEntities",
        DivSetOfCardinalityID: "setOfCardinality",
        DivElementsByDegreeID: "elementsByDegree",
        LogListElementID: "log",
        SelectionAlignConnectionArc: "left",
        SelectionAlignHistogram: "center",
        HighlightColor: "#D64741",
        BlurredHighlightColor: "#D38683",
        EntityRelationPreviewColor: "#00f"
    };
    RadSet.Init(options);

    var diaLegend = $("#diaLegend").dialog({
        autoOpen: false,
        modal: false,
        width: 100,
        title: "Legend"
    });

    $("#openLegend").click(function () {
        RadSet.ShowLegend();
    });

    $("#reload").click(function () {
        RadSet.Draw();
    });
    $("#clearSel").click(function () {
        RadSet.ClearSelection();
    });
});


