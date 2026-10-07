# ********************
# Last names: Chan, Cheng, Jugno, Lim
# Language: R
# Paradigm(s): Functional
# ********************
source("R/helper.R")

input <- file("stdin")
on.exit(close(input))

isRunning <- TRUE
currentBalance <- 1000.00
currentCurrency <- "PHP"

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

    if (!(length(initial) == 0 || is.na(initial))){ # Safety Check
        mainChoice <- initial
    } else {
        mainChoice <- -1
    }

    if (mainChoice == 1){
        isValid <- FALSE
        while (!isValid){
            cat("Register Account Name\n")
            cat("Account Name: ")
            name <- readLines(input, n = 1)

            if (nchar(name) == 0){
                cat("Name cannot be empty, Please Try Again!\n\n")
            } else if (check_user(name, users)){
                cat("Account name already exists, Please use a different one!\n\n")
            } else{
                isValid <- TRUE
            }
        }
        userCount <- userCount + 1
        users[userCount] <- name
        bankStatus[[userCount]] <- list(0.0, "PHP") # Setting default acc balance to 0 and default currency to PHP
    } else if (mainChoice == 2){
        isValid <- FALSE
        while (!isValid){
            cat("Deposit Amount\n")
            cat("Account Name: ")
            name <- readLines(input, n = 1)

            if (nchar(name) == 0){
                cat("Name cannot be empty, Please Try Again!\n\n")
            } else if (!check_user(name, users)){
                cat("Account does not exist, Please Try Again!\n\n")
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
            cat("Deposit Amount: ")
            depAmount <- as.numeric(readLines(input, n = 1))
            if (depAmount < 0){
                cat("Deposit amount cannot be negative, Please Try Again!\n\n")
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

            if (nchar(name) == 0){
                cat("Name cannot be empty, Please Try Again!\n\n")
            } else if (!check_user(name, users)){
                cat("Account does not exist, Please Try Again!\n\n")
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
            if (withAmount < 0){
                cat("Withdraw amount cannot be negative, Please Try Again!\n\n")
            } else if (withAmount > currentBalance){
                cat("Insufficient Balance, Please Try Again!\n\n")
            } else {
                isValid <- TRUE
            }
        }
        bankStatus[[index]][balIndex] <- currentBalance - withAmount
        cat("Updated Balance:", sprintf("%.2f", bankStatus[[index]][[balIndex]]), "\n")

    } else if (mainChoice == 4){
        cat("Foreign Currency Exchange\n")
        cat("Source Currency Options:\n")
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
                cat("Invalid Choice, Please Try Again!\n")
            } else {
                while (!isValid){
                    cat("Source Amount: ")
                    srcAmt <- as.numeric(readLines(input, n = 1))
                    if (is.na(srcAmt) || srcAmt < 0){
                        cat("Invalid Amount, Please Try Again!\n")
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
                cat("Invalid Choice, Please Try Again!\n")
            } else {
                isValid <- TRUE
            }
        }
        exchangedAmount <- srcAmt * exchangeRates[[srcCurr]] / exchangeRates[[exCurr]]
        cat("Exchanged Amount:", sprintf("%.2f", exchangedAmount), "\n")
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

            if (chosen_currency < 1 || chosen_currency > 6){
                cat("Invalid Choice, Please Try Again!\n")
            } else {
                while (!isValid){
                    cat("Exchange Rate: ")
                    rate <- as.numeric(readLines(input, n = 1))

                    if (is.na(rate) || rate < 0){
                        cat("Invalid rate, Please Try Again!\n")
                    } else {
                        isValid <- TRUE
                    }
                }
            }
        }
        exchangeRates[[chosen_currency]] <- rate
        
    } else if (mainChoice == 6){
        
    } else if (mainChoice == 7){
        isRunning <- FALSE
    } else {
        cat("Invalid Input, Try Again!\n\n")
    }
}