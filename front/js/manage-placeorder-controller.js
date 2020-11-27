/*==================================================================
*GLOBAL VARIABLE AND CONSTANT
====================================================================*/

/*==================================================================
*DOCUMENT.READY AND WINDOW.LOAD
====================================================================*/

/*==================================================================
*EVENT HANDLER
====================================================================*/
$("#btn-save").click(addOrUpdateOrders);
/*==================================================================
*METHODS
====================================================================*/

function addOrUpdateOrders() {

    var itemCode = $("#txt-item-code").val();
    var itemDescription = $("#txt-item-description").val();
    var unitPrice = $("#txt-item-unit-price").val();
    var buyingQty = $("#txt-item-buying-qty").val();

    var rowHTML = "<tr>" +
        "<td>" + itemCode + "</td>" +
        "<td>" + itemDescription + "</td>" +
        "<td>" + buyingQty + "</td>" +
        "<td>" + unitPrice + "</td>" +
        "<td>" + unitPrice*buyingQty + "</td>" +
        "<td class='bin'><i class=\"fas fa-trash\"></i></td></tr>";

    $("#tbl-orders tbody").append(rowHTML);
}