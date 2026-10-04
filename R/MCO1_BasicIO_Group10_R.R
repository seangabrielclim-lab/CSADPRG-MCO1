
input <- file("stdin")
on.exit(close(input))

isRunning <- TRUE
currentBalance <- 1000.00
currentCurrency <- "PHP"

phpToForeign = c()

while (isRunning){
    cat("Select Transaction:\n")
    cat("[1] Register Account Name\n")
    cat("[2] Deposit Amount\n")
    cat("[3] Withdraw Amount\n")
    cat("[4] Currency Exchange\n")
    cat("[5] Record Exchange Rates\n")
    cat("[6] Show Interest Amount\n\n")
    cat("Choice: ")

    initial <- as.integer(readLines(input, n = 1))

    if (!(length(initial) == 0 || is.na(initial))){ # Safety Check
        mainChoice <- initial
    } else {
        mainChoice <- -1
    }

    cat("\n***\n")
    cat("Choice =", mainChoice, "\n\n")

    if (mainChoice == 1){
        cat("Register Account Name\n")
        cat("Account Name: ")
        name <- readLines(input, n = 1)

        cat("\n***\n")
        cat("Account Name =", name, "\n\n")
    } else if (mainChoice == 2){
        cat("Deposit Amount\n")
        cat("Account Name: ")
        name <- readLines(input, n = 1)
        cat("Current Balance:", sprintf("%.2f", currentBalance), "\n")
        cat("Currency:", currentCurrency, "\n\n")

        cat("Deposit Amount: ")
        depAmount <- as.numeric(readLines(input, n = 1))
        
        cat("\n***\n")
        cat("Account Name =", name, "\n")
        cat("Deposit Amount =", sprintf("%.2f", depAmount), "\n\n")
    } else if (mainChoice == 3){
        cat("Withdraw Amount\n")
        cat("Account Name: ")
        name <- readLines(input, n = 1)
        cat("Current Balance:", sprintf("%.2f", currentBalance), "\n")
        cat("Currency:", currentCurrency, "\n\n")

        cat("Withdraw Amount: ")
        withAmount <- as.numeric(readLines(input, n = 1))

        cat("\n***\n")
        cat("Account Name =", name, "\n")
        cat("Withdraw Amount =", sprintf("%.2f", withAmount), "\n\n")
    } else if (mainChoice == 4){
        cat("Foreign Currency Exchange\n")
        cat("Source Amount (PHP): ")
        srcAmt <- as.numeric(readLines(input, n = 1))

        cat("\nExchanged Currency\n")
        cat("[1] Philippine Peso (PHP) =", sprintf("%.2f", srcAmt), "\n")
        cat("[2] United States Dollar (USD) =", sprintf("%.2f", srcAmt * 62.00), "\n")
        cat("[3] Japanese Yen (JPY) =", sprintf("%.2f", srcAmt * 0.40), "\n")
        cat("[4] British Pound Sterling (GBP) =", sprintf("%.2f", srcAmt * 84.00), "\n")
        cat("[5] Euro (EUR) =", sprintf("%.2f", srcAmt * 72.00), "\n")
        cat("[6] Chinese Yuan Renminni (CNY) =", sprintf("%.2f", srcAmt * 9.00), "\n")

        cat("\n***\n")
        cat("Source Currency = Philippine Peso (PHP)\n")
        cat("Source Amount (PHP) =", sprintf("%.2f", srcAmt), "\n\n")

    } else if (mainChoice == 5){
        cat("Record Exchange Rate\n\n")
        cat("[1] Philippine Peso (PHP)\n")
        cat("[2] United States Dollar (USD)\n")
        cat("[3] Japanese Yen (JPY)\n")
        cat("[4] British Pound Sterling (GBP)\n")
        cat("[5] Euro (EUR)\n")
        cat("[6] Chinese Yuan Renminni (CNY)\n\n")

        cat("Select Foreign Currency: ")
        chosen_currency <- readLines(input, n = 1)
        cat("Exchange Rate: ")
        rate <- as.numeric(readLines(input, n = 1))

        if (!startsWith(chosen_currency, "[")){
            chosen_currency <- paste0("[", chosen_currency, "]")
        }

        cat("\n***\n")
        cat("Select Foreign Currency =", chosen_currency, "\n")
        cat("Exchange Rate =", sprintf("%.2f", rate), "\n\n")
    } else if (mainChoice == 6){
        
    } else if (mainChoice == 7){
        isRunning <- FALSE
    } else {
        cat("Invalid Input, Try Again!\n\n")
    }
}