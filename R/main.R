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

read_line <- function(){
    line <- readLines(input, n = 1)
    if (length(line) == 0){
        cat("Thank you for banking with us!\n")
        close(input)
        quit(status = 0)
    }
    return (line)
}

validate_input <- function(msg, low, high){
    isValid <- FALSE
    while (!isValid){
        cat(msg)
        choice <- suppressWarnings(as.integer(read_line()))
        if (is.na(choice) || choice < low || choice > high){
            cat(sprintf("Invalid choice. Enter a number from %d to %d.\n", low, high))
        } else {
            isValid <- TRUE
        }
    }
    return (choice)
}

main_menu <- function(){
    cat("Select Transaction:\n")
    cat("[1] Register Account Name\n")
    cat("[2] Deposit Amount\n")
    cat("[3] Withdraw Amount\n")
    cat("[4] Currency Exchange\n")
    cat("[5] Record Exchange Rates\n")
    cat("[6] Show Interest Amount\n\n")
    
    mainChoice <- validate_input("Choice: ", 1, 6)

    cat("\n")
    return (mainChoice)
}

get_amt <- function(msg){
    isValid <- FALSE
    while (!isValid){
        cat(msg)
        amt <- suppressWarnings(as.numeric(read_line()))
        if (is.na(amt) || amt <= 0 || !is.finite(amt)){
            cat("Invalid amount. Enter a number greater than 0.\n")
        } else {
            isValid <- TRUE
        }
    }
    return (amt)
}

get_acc <- function(users){
    isValid <- FALSE
    while (!isValid){
        cat("Account Name: ")
        name <- read_line()

        if (nchar(name) == 0 || !check_user(name, users)){
            cat("*** Error Invalid Input ***\n\n")
        } else {
            isValid <- TRUE
        }
    }

    return (name)
}

print_details <- function(index){
    currentBalance <- bankStatus[[index]][[balIndex]]
    currentCurrency <- bankStatus[[index]][[currencyIndex]]

    cat("\nCurrent Balance:", sprintf("%.2f", currentBalance), "\n")
    cat("Currency:", currentCurrency, "\n")

    return (currentBalance)
}

print_curr_list <- function(){
    cat("[1] Philippine Peso (PHP)\n")
    cat("[2] United States Dollar (USD)\n")
    cat("[3] Japanese Yen (JPY)\n")
    cat("[4] British Pound Sterling (GBP)\n")
    cat("[5] Euro (EUR)\n")
    cat("[6] Chinese Yuan Renminni (CNY)\n\n")
}

reg_acc <- function(){
    isValid <- FALSE
    while (!isValid){
        cat("Register Account Name\n")
        cat("Account Name: ")
        name <- read_line()

        if (nchar(name) == 0 || check_user(name, users)){
            cat("*** Error Invalid Input ***\n\n")
        } else{
            isValid <- TRUE
        }
    }
    userCount <<- userCount + 1
    users[userCount] <<- name
    bankStatus[[userCount]] <<- list(1000.0, "PHP") # Setting default acc balance to 1000 and default currency to PHP   
}

curr_exchange <- function(){
    isRepeating <- TRUE

    while (isRepeating){
        cat("Foreign Currency Exchange\n")
        cat("Source Currency Option:\n")
        print_curr_list()

        srcCurr <- validate_input("Source Currency: ", 1, 6)
        srcAmt <- get_amt("Source Amount: ")    

        cat("Exchanged Currency Options:\n")
        print_curr_list()

        exCurr <- validate_input("Exchange Currency: ", 1, 6)
        
        exchangedAmount <- srcAmt * exchangeRates[[srcCurr]] / exchangeRates[[exCurr]]
        cat("Exchange Amount:", sprintf("%.2f", exchangedAmount), "\n")

        isValid <- FALSE
        while (!isValid){
            cat("\nConvert another currency (Y/N)? . . . ")
            willRepeat <- as.character(read_line())
            if (tolower(willRepeat) == 'n'){
                isRepeating <- FALSE
                isValid <- TRUE
            } else if (tolower(willRepeat) == 'y'){
                cat("\n")
                isValid <- TRUE
            } else{
                cat("Please enter Y or N.\n")
            }
        }
    } 

}

rec_rate <- function(){
    cat("Record Exchange Rate\n")
    print_curr_list()
    isValid <- FALSE
    while (!isValid){
        chosenCurr <- validate_input("Select Foreign Currency: ", 1, 6)
        if (chosenCurr == 1){
            cat("PHP is the base currency; its rate is fixed at 1.00.\n")
        } else {
            rate <- get_amt("Exchange Rate: ")
            isValid <- TRUE
        }
    }
    exchangeRates[[chosenCurr]] <<- rate

}

get_currency <- function(){
    cat("\n")
    print_curr_list()
    return (validate_input("Transaction Currency: ", 1, 6))
}

deposit <- function(){
    cat("Deposit Amount\n")
    name <- get_acc(users)    

    index <- get_user_index(name, users)
    currentBalance <- print_details(index)
    
    srcCurr <- get_currency()
    depAmount <- get_amt("Deposit Amount: ") * exchangeRates[[srcCurr]]
    
    cat(sprintf("Equivalent in PHP (rate %.2f): %.2f\n", exchangeRates[[srcCurr]], depAmount))
    bankStatus[[index]][balIndex] <<- currentBalance + depAmount # Update user bank status
    cat("Updated Balance:", sprintf("%.2f", bankStatus[[index]][[balIndex]]), "\n\n")
}

withdraw <- function(){
    cat("Withdraw Amount\n")
    name <- get_acc(users)

    index <- get_user_index(name, users)
    currentBalance <- print_details(index)

    if (currentBalance == 0){
        cat("Insufficient balance. Withdrawal cancelled.\n") # No balance in bank account to withdraw
    } else{
        srcCurr <- get_currency()
        isValid <- FALSE
        while (!isValid){
            amt <- get_amt("Withdraw Amount: ")
            withAmount <- amt * exchangeRates[[srcCurr]]
            if (withAmount > currentBalance){
                cat("Insufficient balance. Withdrawal cancelled.\n")
            } else {
                isValid <- TRUE
            }
        }
        
        cat(sprintf("Equivalent in PHP (rate %.2f): %.2f\n", exchangeRates[[srcCurr]], withAmount))
        bankStatus[[index]][balIndex] <<- currentBalance - withAmount # Update user's bank account
        cat("Updated Balance:", sprintf("%.2f", bankStatus[[index]][[balIndex]]), "\n\n")    
    }

}

show_interest <- function(){
    cat("Show Interest Amount\n")
    name <- get_acc(users)
    
    index <- get_user_index(name, users)
    currentBalance <- print_details(index)
    cat("Interest Rate: 5%\n\n")

    numDays <- validate_input("Total Number of Days: ", 1, 3650)
    
    cat("Day | Interest | Balance |\n")
    newBal <- currentBalance
    for (i in 1:numDays){
        interest <- newBal * (defaultRate / 365)
        newBal <- newBal + interest
        cat(sprintf("%-3d | %-8.2f | %-10.2f |\n", i, interest, newBal))
    }
}

# Main Program Logic

input <- file("stdin", "r")

isRunning <- TRUE
defaultRate <- 0.05

users <- c()
userCount <- 0

bankStatus <- list()
balIndex <- 1
currencyIndex <- 2

exchangeRates <- c(1.0, 62.0, 0.4, 84.0, 72.0, 9.0) # default values


while (isRunning){
    mainChoice <- main_menu()

    if (mainChoice == 1){
        reg_acc()
    } else if (mainChoice == 4){
        curr_exchange()
    } else if (mainChoice == 5){
        rec_rate()
    } else if (length(users) == 0) { # There are no registered users, therefore cannot allow the other transactions
        cat("No account registered yet. Please register an account name first.\n")
    } else if (mainChoice == 2){
        deposit()
    } else if (mainChoice == 3){
        withdraw()
    }  else if (mainChoice == 6){
        show_interest()
    }

    exited <- FALSE
    while (!exited && isRunning){
        cat("Back to the Main Menu (Y/N): ")
        exitInput <- as.character(read_line())

        if (tolower(exitInput) == 'y'){
            exited <- TRUE
        } else if (tolower(exitInput) == 'n'){
            cat("Thank you for banking with us!\n")
            isRunning <- FALSE
        } else {
            cat("Please enter Y or N.\n")
        }
    }
}
close(input)
