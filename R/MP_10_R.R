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

suppressPackageStartupMessages({
    library(dplyr)
    library(readr)
    library(jsonlite)
    library(tibble)
})
options(pillar.width = Inf, pillar.sigfig = 12)

# MCO1 FUNCTIONS: 
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
    cat("[6] Show Interest Amount\n")
    cat("[7] Generate Reports\n\n")

    
    mainChoice <- validate_input("Choice: ", 1, 7)

    cat("\n")
    return (mainChoice)
}

# This function validates a monetary amount or exchange rate (only positive real numbers are accepted)
# This function will keep prompting the user until they enter a valid input
# Parameter:
# msg - a string that gives the user the description of the input being asked
get_amt <- function(msg, min_val = 0, inclusive = FALSE){
    isValid <- FALSE
    while (!isValid){
        cat(msg)
        amt <- suppressWarnings(as.numeric(read_line()))
        isValidNum <- !is.na(amt) && is.finite(amt) && (if(inclusive) (amt >= min_val) else (amt > min_val))
        if (!isValidNum){
            if (inclusive){
                cat(sprintf("Invalid amount. Enter a number at least %.0f.\n", min_val))
            } else {
                cat("Invalid amount. Enter a number greater than 0.\n")
            }
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

    if (length(users) == 0 && file.exists("bank_transactions.csv")) {
        transactions_df <<- clean_data(load_data("bank_transactions.csv"))
        update_df(transactions_df)
    }


    isValid <- FALSE
    while (!isValid){
        cat("Register Account Name\n")
        cat("Account Name: ")
        name <- trimws(read_line())

        if (!is_valid_name(name)){
            cat("Invalid name. Enter your first name and last name (e.g. Juan Dela Cruz).\n")
        }else if (check_user(name, users)){
            cat("An account with this name is already registered.\n")
        } else{
            initBalance <- get_amt("Initial Deposit: ", min_val = 5000, inclusive = TRUE)
            
            max_id <- if (length(users_numbers) > 0) max(as.numeric(users_numbers), na.rm = TRUE) else 1092000000
            acc_number <- sprintf("%010.0f", max_id + 1)

            userCount <<- userCount + 1
            users[userCount] <<- name
            users_numbers[userCount] <<- acc_number
            bankStatus[[userCount]] <<- list(initBalance, "PHP")

            add_transaction(name, acc_number, initBalance, "deposit", initBalance)
            cat("Account Created Successfully!\n\n")
            isValid <- TRUE
        }
    }
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
    newBalance <- currentBalance + depAmount
    
    cat(sprintf("Equivalent in PHP (rate %.2f): %.2f\n", exchangeRates[[srcCurr]], depAmount))
    bankStatus[[index]][balIndex] <<- newBalance # Update user bank status
    add_transaction(users[index], users_numbers[index], depAmount, "deposit", newBalance)
    cat("Updated Balance:", sprintf("%.2f", newBalance), "\n\n")
}

# This function allows the user to withdraw from their bank account in the selected currency
# It first converts the amount to php and then subtracts it from their current bank balance
withdraw <- function(){
    cat("Withdraw Amount\n")
    name <- get_acc(users)

    index <- get_user_index(name, users)
    currentBalance <- print_details(index)

    if (currentBalance == 0) {
        cat("Insufficient balance. Withdrawal cancelled.\n")
    } else {
        srcCurr <- get_currency()
        isValid <- FALSE
        while (!isValid) {
            amt <- get_amt("Withdraw Amount: ")
            withAmount <- amt * exchangeRates[[srcCurr]]
            if (withAmount > currentBalance) {
                cat("Insufficient balance. Withdrawal cancelled.\n")
            } else {
                isValid <- TRUE
            }
        }

        newBalance <- currentBalance - withAmount
        cat(sprintf("Equivalent in PHP (rate %.2f): %.2f\n", exchangeRates[[srcCurr]], withAmount))
        bankStatus[[index]][balIndex] <<- newBalance
        add_transaction(users[index], users_numbers[index], withAmount, "withdrawal", newBalance)
        cat("Updated Balance:", sprintf("%.2f", newBalance), "\n\n")
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
        interest <- round(newBal * (defaultRate / 365), 2)
        newBal <- newBal + interest
        cat(sprintf("%-3d | %-8.2f | %-10.2f |\n", i, interest, newBal))
    }
}

# MCO2 FUNCTIONS:
print_log <- function(prompt, value){
    cat(sprintf(prompt, value))
}

load_data <- function(path) {
    df <- read_csv(path, 
                    col_types = cols(.default = col_character()),
                    show_col_types = FALSE)

    return (df)
}

clean_data <- function(df){
    df  <- df %>%
        mutate(currency = toupper(trimws(currency)), type = tolower(trimws(type)),
                account_name = trimws(account_name)) %>%
        filter(currency == "PHP")

    df <- df %>%
        mutate(date = as.Date(trimws(date), format = "%d/%m/%Y"),
                amount = suppressWarnings(as.numeric(gsub(",", "", trimws(amount)))),
                current_balance = suppressWarnings(as.numeric(gsub(",", "", trimws(current_balance))))
                )
    df <- df %>% 
        filter(!is.na(account_name), !is.na(account_number), !is.na(date), !is.na(amount),
                !is.na(current_balance), type %in% c("deposit", "withdrawal"))

    df <- df %>%
        arrange(as.POSIXct(paste(date, time), format = "%Y-%m-%d %H:%M:%S"))

    return (df)
}

get_daily_balances <- function(df){
    # Gets the date for the first transaction per account
    data_frame <- df %>%
        group_by(account_number) %>%
        summarize(date_of_first_transaction = min(date))

    # Gets the last transaction per account per day and sets that as the end of the day balance
    eod_df <- df %>%
        group_by(account_number, date) %>%
        slice_tail(n = 1) %>%
        ungroup() %>%
        select(account_name, account_number, date, end_of_day_balance = current_balance)
    
    # Generates a table of dates starting from the earliest transaction upto the latest, incrementing it by day
    latest_date = max(df$date)
    start_this_month = as.Date(format(latest_date, "%Y-%m-01"))
    end_this_month = seq(start_this_month, by = "1 month", length.out = 2) [2] - 1
    days <- tibble(date = seq(min(df$date), end_this_month, by = "day"))

    # Combines the Tables
    data_frame <- data_frame %>% 
        cross_join(days) %>%
        filter(date >= date_of_first_transaction) %>%
        left_join(eod_df, by = c("account_number", "date")) %>%
        arrange(account_number, date)
    
    # Fills in the balances of days without transaction
    data_frame <- data_frame %>%
        group_by(account_number) %>%
        mutate(last_row = cummax(ifelse(is.na(end_of_day_balance), 0, row_number())), end_of_day_balance = end_of_day_balance[last_row], account_name = account_name[last_row]) %>%
        ungroup() %>%
        select(account_name, account_number, date, end_of_day_balance)

    return (data_frame)
}

get_privilege_tiers <- function(df, latest_date){
    start_this_month <- as.Date(format(latest_date, "%Y-%m-01"))
    end_this_month <- seq(start_this_month, by = "1 month", length.out = 2)[2] - 1
    days_in_month <- as.numeric(end_this_month - start_this_month) + 1

    data_frame <- df %>%
        group_by(account_name, account_number) %>%
        summarize(first_day = min(date), current_balance = last(end_of_day_balance),
                    AverageDailyBalance = sum(end_of_day_balance[date >= start_this_month & date <= end_this_month]) / days_in_month,
                    .groups = "drop") %>%
        filter(AverageDailyBalance >= 100000.00) %>%
        mutate(AccountName = account_name, CurrentBalance = current_balance,
                AccountAge = as.numeric(latest_date - first_day),
                PrivilegeTier = case_when(AverageDailyBalance < 500000.00 ~ "Silver", AverageDailyBalance < 1000000.00 ~ "Gold", .default = "Platinum")) %>%
        arrange(desc(AverageDailyBalance)) %>%
        select(AccountName, AccountAge, CurrentBalance, AverageDailyBalance, PrivilegeTier)

    return (data_frame)
}

get_interest_report <- function(df){

    data_frame <- df %>%
        group_by(account_number) %>%
        slice_tail(n = 1) %>%
        ungroup() %>%

        filter(current_balance >= 10000.00) %>%
        rowwise() %>%
        mutate(ExpectedInterest = {
            bal <- current_balance
            for (i in 1:30){
                bal <- bal + round(bal * (0.05/365), 2)
            }
            bal - current_balance
        }) %>%
        ungroup() %>%
        arrange(desc(ExpectedInterest)) %>%
        select(AccountName = account_name, CurrentBalance = current_balance, ExpectedInterest)
    
    return (data_frame)
}

get_suspicious_report <- function(df){
    data_frame <- df %>%
        group_by(account_name, account_number, date) %>%
        summarize(TotalWithdrawalAmounts = sum(amount[type == "withdrawal"]), 
                    NumberOfDeposits = sum(type == "deposit"), 
                    TotalDepositAmounts = (sum(amount[type == "deposit"])),
                    .groups = "drop") %>%
        mutate(TurnoverVelocity = ifelse(TotalDepositAmounts == 0, 0, (TotalWithdrawalAmounts / TotalDepositAmounts) * 100), 
                RiskFlagLevel = ifelse(TurnoverVelocity >= 90 | TotalWithdrawalAmounts >= 500000.00, "Suspicious", "Normal")) %>%
        arrange(date, desc(RiskFlagLevel), account_name) %>%
        mutate(AccountName = account_name, Date = date) %>%
        select(AccountName, Date, TotalWithdrawalAmounts, NumberOfDeposits, TotalDepositAmounts, TurnoverVelocity, RiskFlagLevel)
    
    return (data_frame)
}

get_statistical_report <- function(df){
    data_frame <- df %>%
        group_by(account_name, account_number) %>%
        summarize(MeanBalance = mean(end_of_day_balance), MedianBalance = median(end_of_day_balance),
                    AccountStandardDev = ifelse(n() > 1, sd(end_of_day_balance), 0),
                    .groups = "drop") %>%
        mutate(StabilityLevel = ifelse(MeanBalance == 0, 0, (AccountStandardDev / MeanBalance) * 100),
                StabilityProfile = case_when(StabilityLevel >= 75 ~ "Unstable", StabilityLevel >= 25 ~ "Moderate", .default = "Stable")) %>%
        arrange(desc(StabilityLevel), account_number) %>%
        select(AccountName = account_name, AccountNumber = account_number, MedianBalance, AccountStandardDev, StabilityLevel, StabilityProfile)
    
    return (data_frame)
}

generate_summary <- function(df, interest_report){
    account_totals <- df %>%
        summarize(total_accounts = n_distinct(account_number, na.rm = TRUE), total_deposits = sum(amount[type == "deposit"], na.rm = TRUE),
                    total_withdrawals = sum(amount[type == "withdrawal"], na.rm = TRUE))
    
    bal_total <- df %>%
        group_by(account_number) %>%
        slice_tail(n = 1) %>%
        ungroup() %>%
        summarize(total_balance = sum(current_balance, na.rm = TRUE), .groups = "drop")

    interest_df <- interest_report %>%
        summarize(total_interest_expense = sum(ExpectedInterest, na.rm = TRUE))

    bind_cols(account_totals, bal_total, interest_df)
}

add_transaction <- function(acc_name, acc_number, amt, transaction_type, new_balance){
    date_format = format(Sys.Date(), "%d/%m/%Y")
    time_format = format(Sys.time(), "%H:%M:%S")
    
    new_entry <- tibble(
        account_name = acc_name,
        account_number = acc_number,
        date = date_format,
        time = time_format,
        currency = "PHP",
        amount = format_numeric(amt),
        type = transaction_type,
        current_balance = format_numeric(new_balance)
    )
    write_csv(new_entry, "bank_transactions.csv", append = TRUE)

    new_row <- tibble(
        account_name = acc_name,
        account_number = acc_number,
        date = Sys.Date(),
        time = time_format,
        currency = "PHP",
        amount = amt,
        type = transaction_type,
        current_balance = new_balance
    )

    transactions_df <<- bind_rows(transactions_df, new_row)
}

update_df <- function(df){
    accounts <- transactions_df %>%
    group_by(account_number) %>%
    slice_tail(n = 1) %>%
    ungroup()

    users <<- accounts$account_name 
    users_numbers <<- accounts$account_number 
    userCount <<- nrow(accounts) 

    bankStatus <<- list()
    if (userCount > 0){
        for (i in 1:userCount){
            bankStatus[[i]] <<- list(accounts$current_balance[i], accounts$currency[i])
        }
    }
}

generate_reports_menu <- function(){
    isValid <- FALSE
    while (!isValid){
        cat("Generate Reports\n")
        cat("[1] Load the file\n")
        cat("[2] Generate Reports\n")
        reportChoice <- validate_input("Enter Choice: ", 1, 2)

        if (reportChoice == 1){
            transactions_df <<- clean_data(load_data("bank_transactions.csv"))
            update_df(transactions_df)
            isValid <- TRUE
        } else {
            if (nrow(transactions_df) == 0) {
                cat("No transactions have been made yet. Unable to generate reports.\n")
                break
            }
            generate_reports(transactions_df)
            isValid <- TRUE
        }

    }
}

generate_reports <- function(df){
    latest_date <- max(df$date)

    cat("\nGenerating reports...\n")
    # ---- REPORT 1 ----
    daily_balances <- get_daily_balances(df)
    priv_tiers <- get_privilege_tiers(daily_balances, latest_date)
    report1 <- priv_tiers %>%
    mutate(CurrentBalance = format_numeric(CurrentBalance), AverageDailyBalance = format_numeric(AverageDailyBalance))

    write_csv(report1, "report1_banking_privilege_tiers.csv")

    # ---- REPORT 2 ----
    interest_report <- get_interest_report(df)
    report2 <- interest_report %>%
        mutate(CurrentBalance = format_numeric(CurrentBalance), ExpectedInterest = format_numeric(ExpectedInterest))

    write_csv(report2, "report2_interest_expense_projection.csv")

    # ---- REPORT 3 ----
    suspicious_report <- get_suspicious_report(df)
    report3 <- suspicious_report %>%
        mutate(TotalWithdrawalAmounts = format_numeric(TotalWithdrawalAmounts), TotalDepositAmounts = format_numeric(TotalDepositAmounts),
                TurnoverVelocity = format_numeric(TurnoverVelocity))

    write_csv(report3, "report3_suspicious_client_behavior.csv")

    # ---- REPORT 4 ----
    stats_report <- get_statistical_report(daily_balances)
    report4 <- stats_report %>%
        mutate(MedianBalance = format_numeric(MedianBalance), AccountStandardDev = format_numeric(AccountStandardDev), 
                StabilityLevel = format_numeric(StabilityLevel))

    write_csv(report4, "report4_stats_report_on_account.csv")

    
    # ---- SUMMARY ----   
    summary <- generate_summary(df, interest_report)
    summary <- summary %>%
        mutate(total_deposits = round(total_deposits, 2), total_withdrawals = round(total_withdrawals, 2), 
                total_balance = round(total_balance, 2), total_interest_expense = round(total_interest_expense, 2))
    summary_json <- toJSON(as.list(summary), pretty = TRUE, auto_unbox = TRUE)
    writeLines(summary_json, "summary.json")

    cat("Outputs saved to individual files...\n")

    cat("\nReport 1: Banking Privilege Tiers\n")
    for (i in seq_len(min(2, nrow(report1)))) {
        cat(sprintf("%s | %d | %s | %s | %s\n",
            report1$AccountName[i], report1$AccountAge[i],
            report1$CurrentBalance[i], report1$AverageDailyBalance[i], report1$PrivilegeTier[i]))
    }
    cat("\n(Full table exported to report1_banking_privilege_tiers.csv)\n")

    cat("\nReport 2: Interest Expense Projection\n")
    for (i in seq_len(min(2, nrow(report2)))) {
        cat(sprintf("%s | %s | %s\n",
            report2$AccountName[i], report2$CurrentBalance[i], report2$ExpectedInterest[i]))
    }
    cat("\n(Full table exported to report2_interest_expense_projection.csv)\n")

    cat("\nReport 3: Suspicious Client Behavior\n")
    for (i in seq_len(min(2, nrow(report3)))) {
        cat(sprintf("%s | %s | %s | %d | %s | %s | %s\n",
            report3$AccountName[i], as.character(report3$Date[i]),
            report3$TotalWithdrawalAmounts[i], report3$NumberOfDeposits[i],
            report3$TotalDepositAmounts[i], report3$TurnoverVelocity[i], report3$RiskFlagLevel[i]))
    }
    cat("\n(Full table exported to report3_suspicious_client_behavior.csv)\n")

    cat("\nReport 4: Balance Volatility & Stability Profile\n")
    for (i in seq_len(min(2, nrow(report4)))) {
        cat(sprintf("%s | %s | %s | %s | %s | %s\n",
            report4$AccountName[i], report4$AccountNumber[i],
            report4$MedianBalance[i], report4$AccountStandardDev[i],
            report4$StabilityLevel[i], report4$StabilityProfile[i]))
    }
    cat("\n(Full table exported to report4_stats_report_on_account.csv)\n")

    cat("\nSummary State (summary.json):\n")
    cat(summary_json, "\n")



}

format_numeric <- function(value){
    return (sprintf("%.2f", value))
}

is_valid_name <- function(name){
   grepl("^[A-Za-z][A-Za-z.'-]*( [A-Za-z][A-Za-z.'-]*)+$", trimws(name))
}


# Main Program Logic

transactions_df <- clean_data(load_data("bank_transactions.csv"))
input <- file("stdin", "r")

accounts <- transactions_df %>%
    group_by(account_number) %>%
    slice_tail(n = 1) %>%
    ungroup()


# Initializes the variables and data structures needed for the program
isRunning <- TRUE
defaultRate <- 0.05

users <- accounts$account_name # a vector that stores the registerd account names
users_numbers <- accounts$account_number # a vector that stores the registerd account numbers
userCount <- nrow(accounts) # counts the number of registered users

bankStatus <- list() # Stores the user's bank details, the details is represented in a list (balance, currency)

if (userCount > 0){
    for (i in 1:userCount){
        bankStatus[[i]] <- list(accounts$current_balance[i], accounts$currency[i])
    }
}

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
    } else if (mainChoice == 7){
        generate_reports_menu()
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
