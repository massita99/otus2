$(document).ready(function() {

    function ajaxSaveRequest(f_method, f_url, f_data) {
        //$("#dataSent").val(unescape(f_data));
        var f_contentType = 'application/json';
        $.ajax({
            url: f_url,
            type: f_method,
            contentType: f_contentType,
            dataType: 'json',
            data: JSON.stringify(f_data),
            success: function(data) {
                $("#results").val(unescape(data.value));
            }
        });
    }

    function ajaxLoadRequest(f_method, f_url, f_data) {
        //$("#dataSent").val(unescape(f_data));
        var f_contentType = 'application/json';
        $.ajax({
            url: f_url,
            type: f_method,
            contentType: f_contentType,
            dataType: 'json',
            data: f_data,
            success: function(data) {
                $("#loadForm").find("[name='name']").val(data.name);
                $("#loadForm").find("[name='age']").val(data.age);
                $("#loadForm").find("[name='address[street]']").val(data.address.street);

                var firstPhone = $("#loadForm").find("[name='phone[0][number]']");
                var newPhone;
                $.each(firstPhone, function(index, item) {
                    if (index > 0) {
                        item.remove()
                    } else {
                        newPhone = item;
                    }
                });

                var phoneParent = firstPhone.parent();
                data.phones.forEach(function(entry) {
                    var newEntry = $(newPhone).clone().appendTo(phoneParent);
                    newEntry.val(entry.number);
                    firstPhone.remove()
                });
            },
            error: function (data) {
                alert(data.responseJSON.value)
            }
        });
    }

    function ajaxStatRequest(f_method, f_url, f_data) {
        //$("#dataSent").val(unescape(f_data));
        var f_contentType = 'application/json';
        $.ajax({
            url: f_url,
            type: f_method,
            contentType: f_contentType,
            dataType: 'json',
            data: f_data,
            success: function(data) {
                $("#statForm").find("[name='count']").val(data.count);

            },
            error: function (data) {
                alert(data.message)
            }
        });
    }

    $(document).on('click', '.btn-add', function(event) {
        event.preventDefault();
        var controlForm = $('.controls');
        var currentEntry = $(this).parents('.entry:first');
        var newEntry = $(currentEntry.clone()).appendTo(controlForm);
        newEntry.find('input').val('');
        controlForm.find('.entry:not(:last) .btn-add')
            .removeClass('btn-add').addClass('btn-remove')
            .removeClass('btn-success').addClass('btn-danger')
            .html('<span class="glyphicon glyphicon-minus"></span>');

        var inputs = $('.controls .form-control');
        $.each(inputs, function(index, item) {
            item.name = 'phones[' + index + '][number]';
        });
    });

    $(document).on('click', '.btn-remove', function(event) {
        event.preventDefault();
        $(this).parents('.entry:first').remove();
        var inputs = $('.controls .form-control');
        $.each(inputs, function(index, item) {
            item.name = 'phones[' + index + ']';
        });
    });

    $(document).on('click', '.btn-remove', function(event) {
        e.preventDefault();
        alert('remove');
    });

    $("#loadUser").click(function(event) {
        event.preventDefault();
        var form = $('#loadForm');
        var method = form.attr('method');
        var url = form.attr('action');
       /* var url = "/ajaxRequest/getData/";*/
        var data = {id: form.find('input[name="id"]').val()};
        console.log(data);
        ajaxLoadRequest(method, url, data);
    });

    $("#getStat").click(function(event) {
        event.preventDefault();
        var form = $('#statForm');
        var method = form.attr('method');
        var url = form.attr('action');
        /* var url = "/ajaxRequest/getData/";*/
        var data = {stat: "count"};
        console.log(data);
        ajaxStatRequest(method, url, data);
    });

    $("#saveUser").click(function(event) {
        event.preventDefault();
        var form = $('#saveForm');
        var method = form.attr('method');
        var url = form.attr('action');
        var jsonData = $(form).serializeObject();
        console.log(jsonData);
        ajaxSaveRequest(method, url, jsonData);
    });

    /*    $.mockjax({
        url: '/ajaxRequest/getData/',
        type: 'GET',
        contentType: 'application/json',
        responseTime: 0,
        response: function(settings) {
            var data = {
                id:1,
                name: "vaseya",
                age: "123",
                address: {street: "123"},
                phones: [
                    {number: "1234"},
                    {number: "5678"}
                ]
            };
            this.responseText = data;
        }
    });*/


});

