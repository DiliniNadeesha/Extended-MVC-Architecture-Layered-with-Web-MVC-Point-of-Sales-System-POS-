/*GLOBAL VARIABLE AND CONSTANT*/
var tblCustomers = null;
var selectedRow = null;
/*Document.ready and Window.load*/
$(function () {
    $("#txt-id").focus();
    initializeDataTable();
});

/*event handler*/
$("#btn-save").click(saveOrUpdateCustomer);
$("#btn-clear").click(deSelectAllCustomers);
$("#tbl-customers tbody").on("click", "tr", selectCustomer);
$("#tbl-customers tbody").on("click", "tr .bin", deleteCustomer);
$("#txt-id, #txt-name, #txt-address").keypress(validationListener);

/*Methods*/
function initializeDataTable(callBackFn) {
    if (tblCustomers != null) {
        tblCustomers.destroy();
    }
    if (callBackFn != undefined) {
        callBackFn();
        if ($("#tbl-customers tbody tr").length > 0) {
            $("#tbl-customers tfoot").addClass('d-none');
        } else {
            $("#tbl-customers tfoot").removeClass('d-none');
        }
    }
    tblCustomers = $("#tbl-customers").DataTable({
        "lengthChange": false,
        "pageLength": 4,
        "info": false,
        "responsive": true,
        "autoWidth": false,
    });

    $("#tbl-customers tr .dataTables_empty").remove();
}

function saveOrUpdateCustomer() {

    var id = $("#txt-id").val();
    var name = $("#txt-name").val();
    var address = $("#txt-address").val();

    var validated = true;
    var selectors = [];

    if (address.trim().length < 3) {
        $("#txt-address").select();
        $("#txt-address").addClass("is-invalid");
        validated = false;
    }

    if (!/[A-Za-z ]{3,}/.test(name)) {
        $("#txt-name").select();
        $("#txt-name").addClass("is-invalid");
        validated = false
    }
    if (!/^C\d{3}$/.test(id)) {
        $("#txt-id").select();
        $("#txt-id").addClass("is-invalid");
        validated = false;
    }

    if (!validated) {
        $("form .is-invalid").tooltip('show');
        return;
    }

    if ($("#btn-save").text() === "Update") {
        selectedRow.find("td:nth-child(2)").text(name);
        selectedRow.find("td:nth-child(3)").text(address);
        $("#btn-clear").click();
        return;
    }

    /*1. Create a XMlHTTPRequest*/
    var http = new XMLHttpRequest();

    /*2 . Async all callback functions*/
    http.onreadystatechange = function () {
        if (http.readyState === 4) {
            if (http.status === 201) {

                 var rawHTML = "<tr>" +
                     "<td>" + id + "</td>" +
                     "<td>" + name + "</td>" +
                     "<td>" + address + "</td>" +
                     "<td class='bin'><i class=\"fas fa-trash\"></i></td></tr>";


                 initializeDataTable(function () {
                     $("#tbl-customers tbody").append(rawHTML);
                 });
                $("#btn-clear").click();
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Oops...',
                    text: 'Something went wrong!Failed to save the customer',
                });
                $("#btn-clear").click();
                $("#txt-id").select();
            }
        }
    }
    /*3. It is time to open the request*/
    http.open("POST", "http://localhost:8080/myapp/customers", true);

    http.setRequestHeader("Content-Type","application/x-www-form-urlencoded");

    /*4. Let's set some headers for the request*/
   // var queryString = 'id=' + id + '&name=' + name + '&address=' + address;
   //                ||
    var queryString = $("form").serialize();

    /*5. Let's send the request*/
    http.send(queryString);

}

function selectCustomer() {
    deSelectAllCustomers();
    $(this).addClass("selected-row");

    selectedRow = $(this);
    $("#txt-id").val(selectedRow.find($("td:first-child")).text());
    $("#txt-name").val(selectedRow.find($("td:nth-child(2)")).text());
    $("#txt-address").val(selectedRow.find($("td:nth-child(3)")).text());
    $("#btn-save").text("Update");
    $("#txt-id").attr("disabled", true);
}

function deSelectAllCustomers() {
    selectedRow = null;
    $("#tbl-customers tbody tr").removeClass("selected-row");
    $("#btn-save").text("Save");
    $("#txt-id").attr("disabled", false);
    removeAllValidation();
}

function deleteCustomer() {
    Swal.fire({
        title: 'Are you sure do you want to delete the customer?',
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
            selectedRow.fadeOut(500, function () {
                initializeDataTable(function () {
                    selectedRow.remove();
                    Swal.fire(
                        'Deleted!',
                        'Customer has been deleted.',
                        'success'
                    )
                });
            });
        } else {
            $(this).parents("tr").removeClass("selected-row");
        }
        deSelectAllCustomers();
        $("#btn-clear").click();
    });
}

function validationListener() {
    $(this).removeClass("is-invalid");
    $(this).tooltip('hide');
}

function removeAllValidation() {
    $('#txt-id, #txt-name, #txt-address').removeClass("is-invalid");
    $('#txt-id, #txt-name, #txt-address').tooltip('hide');
}