/*==================================================================
*GLOBAL VARIABLE AND CONSTANT
====================================================================*/
var tblItems = null;
var selectedRow = null;

/*==================================================================
*DOCUMENT.READY AND WINDOW.LOAD
====================================================================*/
$(function () {
    $("#txt-code").focus();
    loadAllItems();
})


/*==================================================================
*EVENT HANDLER
====================================================================*/
$("#btn-save").click(saveOrUpdateItem);
$("#btn-clear").click(deSelectAllItems);
$("#tbl-items tbody").on("click", "tr", selectItemRow);
$("#tbl-items tbody").on("click", "tr .bin", deleteItemRow);

// $("#tbl-items tbody tr td").click(selectItemRow);
$('#txt-code, #txt-description, #txt-quantity, #txt-unit-price').keypress(validationListener);

/*==================================================================
*METHODS
====================================================================*/

//table initialization and destroy happening here
function initializeDataTable(callBackFn) {
    if (tblItems != null) {
        tblItems.destroy();
    }
    if (callBackFn != undefined) {
        callBackFn();
        //display the tfoot message
        if ($("#tbl-items tbody tr").length > 0) {
            $("#tbl-items tfoot").addClass('d-none');
        } else {
            $("#tbl-items tfoot").removeClass('d-none');
        }
    }
    tblItems = $("#tbl-items").DataTable({
        "lengthChange": false,
        "pageLength": 6,
        "responsive": true,
        "autoWidth": false,
    });

    //remove datatable by default raws
    $("#tbl-items tr .dataTables_empty").remove();
}

function loadAllItems() {
    $.ajax({
        method: 'GET',
        url: 'http://localhost:8080/myapp/items'
    }).done(function (items) {
        for (var i = 0; i < items.length; i++) {
            var code = items[i].code;
            var description = items[i].description;
            var quantity = items[i].quantity;
            var unitPrice = items[i].unitPrice;

            var rawHTML = "<tr>" +
                "<td>" + code + "</td>" +
                "<td>" + description + "</td>" +
                "<td>" + quantity + "</td>" +
                "<td>" + unitPrice + "</td>" +
                "<td class='bin'><i class=\"fas fa-trash\"></i></td></tr>";

            initializeDataTable(function () {
                $("#tbl-items tbody").append(rawHTML);
            });

            $("#btn-clear").click();
        }
    }).fail(function () {
        Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: 'There are no items',
        });
    })
}

function saveOrUpdateItem() {

    var code = $("#txt-code").val();
    var description = $("#txt-description").val();
    var quantity = $("#txt-quantity").val();
    var unitPrice = $("#txt-unit-price").val();

    var validated = true;

    if (!/^\d+(,\d{3})*(\.\d{2})?$/.test(unitPrice)) {
        $("#txt-unit-price").select();
        //border color set to red
        $("#txt-unit-price").addClass("is-invalid");
        validated = false;
    }

    if (!/^\d+$/.test(quantity)) {
        $("#txt-quantity").select();
        $("#txt-quantity").addClass("is-invalid");
        validated = false;
    }

    if (!/[A-Za-z0-9 ]{5,}/.test(description)) {
        $("#txt-description").select();
        $("#txt-description").addClass("is-invalid");
        validated = false;
    }
    if (!/^I\d{3}$/.test(code)) {
        $("#txt-code").select();
        $("#txt-code").addClass("is-invalid");
        validated = false;
    }

    if (!validated) {
        $("form .is-invalid").tooltip('show');
        return;
    }

    if ($("#btn-save").text() === "Update") {
        $.ajax({
            method: 'PUT',
            url: 'http://localhost:8080/myapp/items?code=' + selectedRow.find("td:first-child").text(),
            data: $("form").serialize()
        }).done(function () {
            Swal.fire({
                position: 'top-end',
                icon: 'success',
                title: 'Successfully updated',
                showConfirmButton: false,
                timer: 1500
            });
            selectedRow.find("td:nth-child(2)").text(description);
            selectedRow.find("td:nth-child(3)").text(quantity);
            selectedRow.find("td:nth-child(4)").text(unitPrice);
            deSelectAllItems();
            $("#btn-clear").click();
        }).fail(function () {
            Swal.fire({
                icon: 'error',
                title: 'Oops...',
                text: 'Something went wrong!Failed to update the item',
            });
        });
        return;
    }

    $.ajax({
        method: 'POST',
        url: 'http://localhost:8080/myapp/items',
        data: $("form").serialize()

    }).done(function () {

        var rawHTML = "<tr>" +
            "<td>" + code + "</td>" +
            "<td>" + description + "</td>" +
            "<td>" + quantity + "</td>" +
            "<td>" + unitPrice + "</td>" +
            "<td class='bin'><i class=\"fas fa-trash\"></i></td></tr>";

        initializeDataTable(function () {
            $("#tbl-items tbody").append(rawHTML);
        });

        $("#btn-clear").click();
        Swal.fire({
            position: 'top-end',
            icon: 'success',
            title: 'Item has been saved',
            showConfirmButton: false,
            timer: 1500
        });

    }).fail(function () {
        Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: 'Something went wrong!Failed to save the customer',
        });
        $("#txt-code").select();
    })

}

function deleteItemRow() {
    Swal.fire({
        title: 'Are you sure do you want to delete the item?',
        text: "You won't be able to revert this!",
        icon: 'warning',
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        showCancelButton: true,
        confirmButtonText: 'Yes, delete it!',
        cancelButtonText: 'No, cancel!',
        reverseButtons: true
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                method: 'DELETE',
                url: 'http://localhost:8080/myapp/items?code=' + selectedRow.find("td:first-child").text()
            }).done(function () {
                selectedRow.fadeOut(500, function () {
                    initializeDataTable(function () {
                        selectedRow.remove();
                        Swal.fire(
                            'Deleted!',
                            'Item has been deleted.',
                            'success'
                        )
                        deSelectAllItems();
                        $("#btn-clear").click();
                    });
                });
            }).fail(function () {
                Swal.fire({
                    icon: 'error',
                    title: 'Oops...',
                    text: 'Something went wrong!Failed to delete the customer',
                });
                $(this).parents("tr").removeClass("selected-row");
            })
        }
    });

}

function selectItemRow() {
    deSelectAllItems();
    $(this).addClass("selected-row");

    selectedRow = $(this);
    $("#txt-code").val(selectedRow.find("td:first-child").text());
    $("#txt-description").val(selectedRow.find("td:nth-child(2)").text());
    $("#txt-quantity").val(selectedRow.find("td:nth-child(3)").text());
    $("#txt-unit-price").val(selectedRow.find("td:nth-child(4)").text());

    $("#btn-save").text("Update");
    $("#btn-save").addClass("btn btn-success");
    $("#txt-code").attr("disabled", true);
}

function deSelectAllItems() {
    $("#tbl-items tbody tr").removeClass("selected-row");
    $("#btn-save").text("Save");
    $("#btn-save").removeClass("btn btn-success");
    $("#btn-save").addClass("btn btn-primary");
    $("#txt-code").attr("disabled", false);
    removeAllValidation();
}

function validationListener() {
    $(this).removeClass("is-invalid");
    $(this).tooltip('hide');
}

function removeAllValidation() {
    $('#txt-code, #txt-description, #txt-quantity, #txt-unit-price').removeClass("is-invalid");
    $('#txt-code, #txt-description, #txt-quantity, #txt-unit-price').tooltip('hide');
}