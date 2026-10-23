# ********************
# Last names: Chan, Cheng, Jugno, Lim
# Language: R
# Paradigm(s): Imperative and Procedural
# ********************

# Banking and Currency Exchange Application
# This program allows users to register bank accounts, deposit and withdraw
# money, convert currencies, update exchange rates, and calculate projected
# daily interest. It uses a command-line interface.

# To run this program, just go to the folder CSADPRG-MCO1 and type cmd in the paths area
# This would take you to the command line where you can enter the command
# Rscript R\MP_10_R.R, to run the program

# This function checks whether a certain username is already taken
# The program is non-case sensitive so Sean is the same as sean
# It returns true if the name is already in users, and false otherwise
# Parameters:
# name - the username that the user entered
# users - the vector where the program stores the registered users
check_user <- function(name, users){
    for (user in users){
        if (tolower(name) == tolower(user)){
            return (TRUE)        
        }
    }
    return (FALSE)
}

# This function returns the index of the user from the user vector
# If the username is not in the vector it returns -1
# Parameter:
# name - the username that the user entered
# users - the vector where the program stores the registered users
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

# This function reads user input from the command line
# If no input is passed or entered, the program will deliberately exit as it reads the EOF
read_line <- function(){
    line <- readLines(input, n = 1)
    if (length(line) == 0){
        cat("Thank you for banking with us!\n")
        close(input)
        quit(status = 0)
    }
    return (line)
}

# This function checks whether a certain input is within the low and high depending on the prompt
# This function will repeat until the user enters a valid input
# Parameters:
# msg - a string that gives the user the description of the input being asked
# low - the lowest value that the user can enter
# high - the highest value that the user can enter
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

# This function prints the options in the main menu, and returns the selected option of the user
# This function will keep prompting the user for an input, until their input is valid
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

# This function validates a monetary amount or exchange rate (only positive real numbers are accepted)
# This function will keep prompting the user until they enter a valid input
# Parameter:
# msg - a string that gives the user the description of the input being asked
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

# This function prompts the user for an existing account name
# It rejects blank names, and names that are not in the users vector
# Parameters:
# users - the vector where the program stores the registered users
get_acc <- function(users){
    isValid <- FALSE
    while (!isValid){
        cat("Account Name: ")
        name <- read_line()

        if (nchar(name) == 0 || !check_user(name, users)){
            cat("Name does not exist. Please Enter an existing user.\n")
        } else {
            isValid <- TRUE
        }
    }

    return (name)
}

# This function displays the selected account's current balance and currency.
# It returns the current balance of the user to be used for updating and calculating
# Parameters:
# index - an integer that maps the registered user to their bank details, which is stored in a list
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

# This function registers a new account
# It only accepts a nonblank unique username
reg_acc <- function(){
    isValid <- FALSE
    while (!isValid){
        cat("Register Account Name\n")
        cat("Account Name: ")
        name <- read_line()

        if (nchar(name) == 0 || check_user(name, users)){
            cat("An account with this name is already registered.\n")
        } else{
            isValid <- TRUE
        }
    }
    userCount <<- userCount + 1
    users[userCount] <<- name
    bankStatus[[userCount]] <<- list(1000.0, "PHP") # Setting default acc balance to 1000 and default currency to PHP   
}

# This function converts an amount from the selected source currency to a destination currency (a currency calculator)
# It also allows the user to perform multiple conversions before going back to the main menu
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

# This function allows the user to update the exchange rate of a certain currency to PHP
# And since PHP is the base currency it cannot be selected and is fixed at 1.00
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
# This function displays the currency options and returns the selected currency number
get_currency <- function(){
    cat("\n")
    print_curr_list()
    return (validate_input("Transaction Currency: ", 1, 6))
}

# This function allows the user to deposit in their selected currency 
# It first converts the amount to php and then updates their bank balance
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

# This function allows the user to withdraw from their bank account in the selected currency
# It first converts the amount to php and then subtracts it from their current bank balance
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

# This function calculates and displays the user's projected daily interest of their current bank balance
# The user will input the amount of days and the program uses a fixed annual rate of 5%
# The calculated values will not affect the user's balance, but just shows its future value
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

# Initializes the variables and data structures needed for the program
isRunning <- TRUE
defaultRate <- 0.05

users <- c() # a vector that stores the registerd account names
userCount <- 0 # counts the number of registered users

bankStatus <- list() # Stores the user's bank details, the details is represented in a list (balance, currency)
balIndex <- 1 # since bankStatus stores lists, balIndex accesses the user's balance
currencyIndex <- 2 # while currencyIndex accesses the user's bank currency

exchangeRates <- c(1.0, 62.0, 0.4, 84.0, 72.0, 9.0) # a vector of exchange rates (from other currency to php) used for calculating conversions, can be changed by the user if they wish to do so


while (isRunning){
    mainChoice <- main_menu()

    if (mainChoice == 1){ # The user chose to register a new account
        reg_acc()
    } else if (mainChoice == 4){ # The user chose currency exchange
        curr_exchange()
    } else if (mainChoice == 5){ # The user chose to change the exchange rates of other currencies to php
        rec_rate()
    # The 3 options precede the other options because they do not require any registered accounts in the vector to be used 
    } else if (length(users) == 0) { # Checks whether there are registered users already
        cat("No account registered yet. Please register an account name first.\n")
    } else if (mainChoice == 2){ # The user opts to deposit to their bank account
        deposit()
    } else if (mainChoice == 3){ # The user opts to withdraw from their bank account
        withdraw()
    }  else if (mainChoice == 6){ # The user opts to view the projected daily interest and balance of their account
        show_interest()
    }

    # This block of code, prompts the user if they wish to go back to the main menu, after every transaction
    # It only allows (y/n)  regardless of case
    # If the user enters Y, they will return to the main menu and is able to select from the features
    # If the user enters N, a good bye message will be displayed and the program will exit
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
close(input) # Closes the input for safety just before the program ends.
