$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip();
    var actions = $("table td:last-child").html();

    // Append table with add row form on add new row button click
    $(".add-new").click(function(){
        $(this).attr("disabled", "disabled");
        var index = $("table tbody tr:last-child").index();
        var row = '<tr>' +
            '<td><input type="text" class="form-control" name="name" id="name"></td>' +
            '<td><input type="text" class="form-control" name="description" id="description"></td>' +
            '<td><input type="text" class="form-control" name="other" id="other"></td>' +
            '<td>' + actions + '</td>' +
            '</tr>';
        $("table").append(row);
        $("table tbody tr").eq(index + 1).find(".add, .edit").toggle();
        $('[data-toggle="tooltip"]').tooltip();
    });

    // Add new column
    $(".add-column").click(function(){
        $("table thead tr").each(function(){
            $(this).find('th:last-child').prev().after('<th>New Column</th>');
        });

        $("table tbody tr").each(function(){
            $(this).find('td:last-child').prev().after('<td><input type="text" class="form-control" name="newColumn"></td>');
        });

        actions = $("table td:last-child").html();
    });

    // Delete column
    $(".delete-column").click(function(){
        $("table thead tr th:last-child").prev().remove();
        $("table tbody tr").each(function(){
            $(this).find('td:last-child').prev().remove();
        });
    });

    // Add row on add button click
    $(document).on("click", ".add", function(){
        var empty = false;
        var input = $(this).parents("tr").find('input[type="text"]');
        input.each(function(){
            if(!$(this).val()){
                $(this).addClass("error");
                empty = true;
            } else{
                $(this).removeClass("error");
            }
        });
        $(this).parents("tr").find(".error").first().focus();
        if(!empty){
            input.each(function(){
                $(this).parent("td").html($(this).val());
            });
            $(this).parents("tr").find(".add, .edit").toggle();
            $(".add-new").removeAttr("disabled");
        }
    });

    // Edit row on edit button click
    $(document).on("click", ".edit", function(){
        $(this).parents("tr").find("td:not(:last-child)").each(function(){
            $(this).html('<input type="text" class="form-control" value="' + $(this).text() + '">');
        });
        $(this).parents("tr").find(".add, .edit").toggle();
        $(".add-new").attr("disabled", "disabled");
    });

    // Delete row on delete button click
    $(document).on("click", ".delete", function(){
        $(this).parents("tr").remove();
        $(".add-new").removeAttr("disabled");
    });
});
