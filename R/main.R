# ********************
# Last names: Chan, Cheng, Jugno, Lim
# Language: R
# Paradigm(s): Imperative
# ********************
check_user <- function(name, users){
    for (user in users){
        if (tolower(name) == tolower(user)){
            return (TRUE)        
        }
    }
    return (FALSE)
}

get_user_index <- function(name, users){
    currentIndex <- 1
    for (user in users){
        if (tolower(name) == tolower(user)){
            return (currentIndex)
        }
        currentIndex <- currentIndex + 1
    }
    return (-1)
}
# Main Program Logic
main <- function(){
    input <- file("stdin", "r")
    on.exit(close(input))

    isRunning <- TRUE
    defaultRate <- 0.05

    users <- c()
    userCount <- 0

    bankStatus <- list()
    balIndex <- 1
    currencyIndex <- 2

    exchangeRates <- c(1.0, 62.0, 0.4, 84.0, 72.0, 9.0) # default values

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

        if (length(initial) != 0 && !is.na(initial)){
            mainChoice <- initial
        } else {
            mainChoice <- -1
        }
        cat("\n")

        if (mainChoice == 1){
            isValid <- FALSE
            while (!isValid){
                cat("Register Account Name\n")
                cat("Account Name: ")
                name <- readLines(input, n = 1)

                if (nchar(name) == 0 || check_user(name, users)){
                    cat("*** Error Invalid Input ***\n\n")
                } else{
                    isValid <- TRUE
                }
            }
            userCount <- userCount + 1
            users[userCount] <- name
            bankStatus[[userCount]] <- list(0.0, "PHP") # Setting default acc balance to 0 and default currency to PHP
        } else if (mainChoice == 4){
            isRepeating <- TRUE

            while (isRepeating){
                cat("Foreign Currency Exchange\n")
                cat("Source Currency Option:\n")
                cat("[1] Philippine Peso (PHP)\n")
                cat("[2] United States Dollar (USD)\n")
                cat("[3] Japanese Yen (JPY)\n")
                cat("[4] British Pound Sterling (GBP)\n")
                cat("[5] Euro (EUR)\n")
                cat("[6] Chinese Yuan Renminni (CNY)\n\n")
                isValid <- FALSE
                while (!isValid){
                    cat("Source Currency: ")
                    srcCurr <- as.integer(readLines(input, n = 1))

                    if (is.na(srcCurr) || srcCurr < 1 || srcCurr > 6){
                        cat("*** Error Invalid Input ***\n")
                    } else {
                        while (!isValid){
                            cat("Source Amount: ")
                            srcAmt <- as.numeric(readLines(input, n = 1))
                            if (is.na(srcAmt) || srcAmt < 0 || !is.finite(srcAmt)){
                                cat("*** Error Invalid Input ***\n")
                            } else {
                                isValid <- TRUE
                            }
                        }
                    }
                }

                cat("Exchanged Currency Options:\n")
                cat("[1] Philippine Peso (PHP)\n")
                cat("[2] United States Dollar (USD)\n")
                cat("[3] Japanese Yen (JPY)\n")
                cat("[4] British Pound Sterling (GBP)\n")
                cat("[5] Euro (EUR)\n")
                cat("[6] Chinese Yuan Renminni (CNY)\n\n")
                isValid <- FALSE
                while (!isValid){
                    cat("Exchange Currency: ")
                    exCurr <- as.integer(readLines(input, n = 1))

                    if (is.na(exCurr) || exCurr < 1 || exCurr > 6){
                        cat("*** Error Invalid Input ***\n")
                    } else {
                        isValid <- TRUE
                    }
                }
                exchangedAmount <- srcAmt * exchangeRates[[srcCurr]] / exchangeRates[[exCurr]]
                cat("Exchange Amount:", sprintf("%.2f", exchangedAmount), "\n")

                isValid <- FALSE
                while (!isValid){
                    cat("\nConvert another currency (Y/N)? . . . ")
                    willRepeat <- as.character(readLines(input, n = 1))
                    if (tolower(willRepeat) == 'n'){
                        isRepeating <- FALSE
                        isValid <- TRUE
                    } else if (tolower(willRepeat) == 'y'){
                        cat("\n")
                        isValid <- TRUE
                    } else{
                        cat("*** Error Invalid Input ***\n\n")
                    }
                }
            } 

        } else if (mainChoice == 5){
            cat("Record Exchange Rate\n\n")
            cat("[1] Philippine Peso (PHP)\n")
            cat("[2] United States Dollar (USD)\n")
            cat("[3] Japanese Yen (JPY)\n")
            cat("[4] British Pound Sterling (GBP)\n")
            cat("[5] Euro (EUR)\n")
            cat("[6] Chinese Yuan Renminni (CNY)\n\n")

            isValid <- FALSE
            while (!isValid){
                cat("Select Foreign Currency: ")
                chosen_currency <- as.integer(readLines(input, n = 1))

                if (is.na(chosen_currency) || (chosen_currency == 1) || (chosen_currency < 2 || chosen_currency > 6)){
                    cat("*** Error Invalid Input ***\n")
                } else {
                    while (!isValid){
                        cat("Exchange Rate: ")
                        rate <- as.numeric(readLines(input, n = 1))

                        if (is.na(rate) || rate <= 0 || !is.finite(rate)){
                            cat("*** Error Invalid Input ***\n")
                        } else {
                            isValid <- TRUE
                        }
                    }
                }
            }
            exchangeRates[[chosen_currency]] <- rate
            
        } else if (length(users) == 0) {
            cat("*** Error Invalid Input ***\n\n")
        } else if (mainChoice == 2){
            isValid <- FALSE
            while (!isValid){
                cat("Deposit Amount\n")
                cat("Account Name: ")
                name <- readLines(input, n = 1)

                if (nchar(name) == 0 || !check_user(name, users)){
                    cat("*** Error Invalid Input ***\n\n")
                } else {
                    isValid <- TRUE
                }
            }
            index <- get_user_index(name, users)
            currentBalance <- bankStatus[[index]][[balIndex]]
            currentCurrency <- bankStatus[[index]][[currencyIndex]]

            cat("\nCurrent Balance:", sprintf("%.2f", currentBalance), "\n")
            cat("Currency:", currentCurrency, "\n")

            isValid <- FALSE
            while (!isValid){
                cat("Deposit Amount: ")
                depAmount <- as.numeric(readLines(input, n = 1))
                if (is.na(depAmount) || depAmount < 0 || !is.finite(depAmount)){
                    cat("*** Error Invalid Input ***\n\n")
                } else {
                    isValid <- TRUE
                }
            }
            
            bankStatus[[index]][balIndex] <- currentBalance + depAmount
            cat("Updated Balance:", sprintf("%.2f", bankStatus[[index]][[balIndex]]), "\n")

        } else if (mainChoice == 3){
            isValid <- FALSE
            while (!isValid){
                cat("Withdraw Amount\n")
                cat("Account Name: ")
                name <- readLines(input, n = 1)

                if (nchar(name) == 0 || !check_user(name, users)){
                    cat("*** Error Invalid Input ***\n\n")
                } else {
                    isValid <- TRUE
                }
            }

            index <- get_user_index(name, users)
            currentBalance <- bankStatus[[index]][[balIndex]]
            currentCurrency <- bankStatus[[index]][[currencyIndex]]

            cat("Current Balance:", sprintf("%.2f", currentBalance), "\n")
            cat("Currency:", currentCurrency, "\n\n")

            isValid <- FALSE
            while (!isValid){
                cat("Withdraw Amount: ")
                withAmount <- as.numeric(readLines(input, n = 1))
                if (is.na(withAmount) || withAmount < 0 || (withAmount > currentBalance) || !is.finite(withAmount)){
                    cat("*** Error Invalid Input ***\n\n")
                } else {
                    isValid <- TRUE
                }
            }
            bankStatus[[index]][balIndex] <- currentBalance - withAmount
            cat("Updated Balance:", sprintf("%.2f", bankStatus[[index]][[balIndex]]), "\n")

        }  else if (mainChoice == 6){
            cat("Show Interest Amount\n")
            isValid <- FALSE

            while (!isValid){
                cat("Account Name: ")
                name <- readLines(input, n = 1)

                if (nchar(name) == 0 || !check_user(name, users)){
                    cat("*** Error Invalid Input ***\n\n")
                } else {
                    isValid <- TRUE
                }
            }
            index <- get_user_index(name, users)
            currentBalance <- bankStatus[[index]][[balIndex]]
            currentCurrency <- bankStatus[[index]][[currencyIndex]]
            cat("Current Balance:", sprintf("%.2f", currentBalance), "\n")
            cat("Currency:", currentCurrency, "\n")
            cat("Interest Rate: 5%\n\n")

            isValid <- FALSE
            while (!isValid){
                cat("Total Number of Days: ")
                numDays <- as.integer(readLines(input, n = 1))

                if (is.na(numDays) || numDays < 1){
                    cat("*** Error Invalid Input ***\n\n")
                } else {
                    isValid <- TRUE
                }
            }
            cat("Day | Interest | Balance |\n")
            newBal <- currentBalance
            for (i in 1:numDays){
                interest <- newBal * (defaultRate / 365)
                newBal <- newBal + interest
                cat(sprintf("%-3d | %-8.2f | %-10.2f |\n", i, interest, newBal))
            }
        } else {
            cat("*** Error Invalid Input ***\n\n")
        }
        exited <- FALSE
        while (!exited && isRunning){
            cat("Back to the Main Menu (Y/N): ")
            exitInput <- as.character(readLines(input, n = 1))

            if (tolower(exitInput) == 'y'){
                exited <- TRUE
            } else if (tolower(exitInput) == 'n'){
                isRunning <- FALSE
            } else {
                cat("*** Error Invalid Input ***\n")
            }
        }
    }
}

main()