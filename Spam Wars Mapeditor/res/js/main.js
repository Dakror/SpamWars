var tiles = ["box", "boxAlt", "boxCoin", "boxCoinAlt", "boxCoinAlt_disabled", "boxCoin_disabled", "boxEmpty", "boxExplosive", "boxExplosiveAlt", "boxExplosive_disabled", "boxItem", "boxItemAlt", "boxItemAlt_disabled", "boxItem_disabled", "boxWarning", "brickWall", "bridge", "bridgeLogs", "castle", "castleCenter", "castleCenter_rounded", "castleCliffLeft", "castleCliffLeftAlt", "castleCliffRight", "castleCliffRightAlt", "castleHalf", "castleHalfLeft", "castleHalfMid", "castleHalfRight", "castleHillLeft", "castleHillLeft2", "castleHillRight", "castleHillRight2", "castleLeft", "castleMid", "castleRight", "dirt", "dirtCenter", "dirtCenter_rounded", "dirtCliffLeft", "dirtCliffLeftAlt", "dirtCliffRight", "dirtCliffRightAlt", "dirtHalf", "dirtHalfLeft", "dirtHalfMid", "dirtHalfRight", "dirtHillLeft", "dirtHillLeft2", "dirtHillRight", "dirtHillRight2", "dirtLeft", "dirtMid", "dirtRight", "door_closedMid", "door_closedTop", "door_openMid", "door_openTop", "fence", "fenceBroken", "grass", "grassCenter", "grassCenter_rounded", "grassCliffLeft", "grassCliffLeftAlt", "grassCliffRight", "grassCliffRightAlt", "grassHalf", "grassHalfLeft", "grassHalfMid", "grassHalfRight", "grassHillLeft", "grassHillLeft2", "grassHillRight", "grassHillRight2", "grassLeft", "grassMid", "grassRight", "ladder_mid", "ladder_top", "liquidLava", "liquidLavaTop", "liquidLavaTop_mid", "liquidWater", "liquidWaterTop", "liquidWaterTop_mid", "lock_blue", "lock_green", "lock_red", "lock_yellow", "rockHillLeft", "rockHillRight", "ropeAttached", "ropeHorizontal", "ropeVertical", "sand", "sandCenter", "sandCenter_rounded", "sandCliffLeft", "sandCliffLeftAlt", "sandCliffRight", "sandCliffRightAlt", "sandHalf", "sandHalfLeft", "sandHalfMid", "sandHalfRight", "sandHillLeft", "sandHillLeft2", "sandHillRight", "sandHillRight2", "sandLeft", "sandMid", "sandRight", "sign", "signExit", "signLeft", "signRight", "snow", "snowCenter", "snowCenter_rounded", "snowCliffLeft", "snowCliffLeftAlt", "snowCliffRight", "snowCliffRightAlt", "snowHalf", "snowHalfLeft", "snowHalfMid", "snowHalfRight", "snowHillLeft", "snowHillLeft2", "snowHillRight", "snowHillRight2", "snowLeft", "snowMid", "snowRight", "stone", "stoneCenter", "stoneCenter_rounded", "stoneCliffLeft", "stoneCliffLeftAlt", "stoneCliffRight", "stoneCliffRightAlt", "stoneHalf", "stoneHalfLeft", "stoneHalfMid", "stoneHalfRight", "stoneHillLeft2", "stoneHillRight2", "stoneLeft", "stoneMid", "stoneRight", "stoneWall", "tochLit", "tochLit2", "torch", "window"];

function addRow() {
    var row = $("#map tr:first-child").clone();

    $("<td>&nbsp</td>").replaceAll(row.find("td"));

    row.appendTo("#map");

    updateListener();
}

function addCol() {
    $("#map tr").each(function() {
        $(this).append("<td>&nbsp;</td>");
    });

    updateListener();
}

function loadBuildBar() {
    $("#build").css("width", (tiles.length * 72) + "px");
    $.each(tiles, function(i, e) {
        $("#build").append("<li title='" + e + "'><img src='res/img/tile/" + e + ".png'></li>");
    });

    $("#build li").click(function() {
        $("#build li").removeClass("active");
        $(this).addClass("active");
    }).hover(function() {
        $("#label").text($(this).attr("title"));
    }).parent().mouseleave(function() {
        $("#label").text("");
    });
}

function updateListener() {
    $("#wrap").mousedown(function(e) {
        e.preventDefault();
    }).bind("contextmenu", function(e) {
        e.preventDefault();
    });

    $("#map").mousedown(function(e) {
        e.preventDefault();
        $(this).addClass("drag " + (e.button == 0 ? "add" : "del"));
    }).mouseup(function() {
        $(this).removeAttr("class");
    });

    $("#map * td").click(function() {
        if ($("#build li.active img").attr("src") == null) return;

        if ($(this).html() == "&nbsp;") $(this).html("<img>");

        $(this).find("img").attr("src", $("#build li.active img").attr("src"));
    }).mousemove(function(e) {
        e.preventDefault();

        if ($("#build li.active img").attr("src") == null) return;

        if ($("#map").hasClass("drag")) {
            if ($("#map").hasClass("add")) $(this).click();
            else $(this).html("&nbsp;");
        }
    });

    $("#map * td").bind("contextmenu", function(e) {
        $(this).html("&nbsp;");
        return false;
    });

    $("#map").css("width", ($("#map tr:first-child td").length * 70 + 2) + "px");
}

function string2Bin(str) {
    var result = [];
    for (var i = 0; i < str.length; i++) {
        result.push(str.charCodeAt(i));
    }
    return result;
}

function saveMap() {
    var data = $("#map tr:first-child td").length + ":" + $("#map tr").length + ";";
    $.each($("#map tr td"), function(i, e) {
        var src = $(e).find("img").attr("src");

        if (src != null) {
            src = src.substring(src.lastIndexOf("/") + 1, src.indexOf(".png"));

            data += tiles.indexOf(src) + 1 + ";";
        } else data += 0 + ";";
    });
    var blob = new Blob([data], {
        type: "text/plain;charset=utf-8"
    });
    saveAs(blob, "map.txt");
}

function loadMap() {
    var data = prompt("Enter the map data to load");
    var width = parseInt(data.substring(0, data.indexOf(":")));
    var height = parseInt(data.substring(data.indexOf(":") + 1, data.indexOf(";")));
    var raw = data.substring(data.indexOf(";") + 1).split(";");
    raw.pop();
    $("#map").html("");

    for (i = 0; i < height; i++) {
        var row = "<tr>";
        for (j = 0; j < width; j++) {
            var r = raw[i * width + j];
            if (r > 0) row += "<td><img src='res/img/tile/" + tiles[r - 1] + ".png'></td>";
            else row += "<td>&nbsp;</td>";
        }
        $("#map").append(row + "</tr>");
    }

    updateListener();
}

$(function() {
    setTimeout("loadBuildBar()", 0);

    updateListener();
});