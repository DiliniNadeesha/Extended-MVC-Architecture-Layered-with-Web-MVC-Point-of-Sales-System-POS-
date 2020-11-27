/*==================================================================
*GLOBAL VARIABLE AND CONSTANT
====================================================================*/
var tblSearchOrder = null;
/*==================================================================
*DOCUMENT.READY AND WINDOW.LOAD
====================================================================*/
$(function () {
    loadAllOrderDetails();
})
/*==================================================================
*EVENT HANDLER
====================================================================*/

/*==================================================================
*METHODS
====================================================================*/

function initializeDataTable(callBackFn) {
    if (tblSearchOrder != null) {
        tblSearchOrder.destroy();
    }
    if (callBackFn != undefined) {
        callBackFn();
        if ($("#tbl-search-order tbody tr").length > 0) {
            $("#tbl-search-order tfoot").addClass('d-none');
        } else {
            $("#tbl-search-order tfoot").removeClass('d-none');
        }
    }
    tblSearchOrder = $("#tbl-search-order").DataTable({
        "lengthChange": false,
        "pageLength": 4,
        "info": false,
        "responsive": true,
        "autoWidth": false,
    });

    $("#tbl-search-order tr .dataTables_empty").remove();
}

function loadAllOrderDetails() {
    $.ajax({
        method: 'GET',
        url: 'http://localhost:8080/myapp/orders'
    }).done(function (orderDetails) {
        for (var i = 0; i < orderDetails.length; i++) {
            var orderId = orderDetails[i].orderId;
            var orderDate = orderDetails[i].orderDate;
            var customerId = orderDetails[i].customerId;
            var customerName = orderDetails[i].customerName;
            var total = orderDetails[i].total;

            var rawHTML = "<tr>" +
                "<td>" + orderId + "</td>" +
                "<td>" + orderDate + "</td>" +
                "<td>" + customerId + "</td>" +
                "<td>" + customerName + "</td>" +
                "<td>" + total + "</td>" +
                "</tr>";

            initializeDataTable(function () {
                $("#tbl-search-order tbody").append(rawHTML);
            });
        }
    }).fail(function () {
        Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: 'There are no orders',
        });
    })
}