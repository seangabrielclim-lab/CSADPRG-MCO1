suppressPackageStartupMessages({
    library(dplyr)
    library(readr)
})
options(pillar.width = Inf, pillar.sigfig = 12)

print_log <- function(prompt, value){
    cat(sprintf(prompt, value))
}

load_data <- function(path) {
    df <- read_csv(path, 
                    col_types = cols(.default = col_character()),
                    show_col_types = FALSE)
    print_log("Total Rows Loaded: %d\n", nrow(df))

    return (df)
}

clean_data <- function(df){
    df  <- df %>%
        mutate(currency = toupper(trimws(currency)), type = tolower(trimws(type)),
                account_name = trimws(account_name)) %>%
        filter(currency == "PHP")
    
    print_log("Rows after filtering to PHP: %d\n", nrow(df))

    df <- df %>%
        mutate(date = as.Date(trimws(date), format = "%d/%m/%Y"),
                amount = suppressWarnings(as.numeric(gsub(",", "", trimws(amount)))),
                current_balance = suppressWarnings(as.numeric(gsub(",", "", trimws(current_balance))))
                )

    invalid_dates <- sum(is.na(df$date))
    invalid_amt <- sum(is.na(df$amount))
    invalid_curr_bal <- sum(is.na(df$current_balance))
    invalid_type <- sum(!(df$type %in% c("deposit", "withdrawal")))

    print_log("Total rows with invalid date: %d\n", invalid_dates)
    print_log("Total rows with invalid amount: %d\n", invalid_amt)
    print_log("Total rows with invalid current balance: %d\n", invalid_curr_bal)
    print_log("Total rows with invalid type: %d\n", invalid_type)

    df <- df %>% 
        filter(!is.na(account_name), !is.na(account_number), !is.na(date), !is.na(amount),
                !is.na(current_balance), type %in% c("deposit", "withdrawal"))

    print_log("Total rows after cleaning: %d\n", nrow(df))

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
    days <- tibble(date = seq(min(df$date), max(df$date), by = "day"))

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

get_interest_report <- function(df){

    data_frame <- df %>%
        group_by(account_number) %>%
        slice_tail(n = 1) %>%
        ungroup() %>%

        filter(current_balance >= 10000.00) %>%
        mutate(expected_interest = current_balance * ((1 + 0.05/365) ^ 30 - 1)) %>%
        arrange(desc(expected_interest)) %>%
        select(account_name, current_balance, expected_interest)
    
    return (data_frame)
}

get_suspicious_report <- function(df){
    data_frame <- df %>%
        group_by(account_name, account_number, date) %>%
        summarize(total_withdrawal_amounts = sum(amount[type == "withdrawal"]), 
                    number_of_deposits = sum(type == "deposit"), 
                    total_deposit_amounts = (sum(amount[type == "deposit"])),
                    .groups = "drop") %>%
        mutate(turnover_velocity = ifelse(total_deposit_amounts == 0, 0, (total_withdrawal_amounts / total_deposit_amounts) * 100), 
                risk_flag_level = ifelse(turnover_velocity >= 90 | total_withdrawal_amounts >= 500000.00, "Suspicious", "Normal")) %>%
        arrange(desc(risk_flag_level), date, account_number) %>%
        select(account_name, date, total_withdrawal_amounts, number_of_deposits, total_deposit_amounts, turnover_velocity, risk_flag_level)
    
    return (data_frame)
}



# ----------- MAIN LOGIC ----------
data_frame <- clean_data(load_data("bank_transactions.csv"))
head(data_frame)

range(data_frame$date, na.rm = TRUE)

cat("----------------------\n")

daily_balances <- get_daily_balances(data_frame)
head(daily_balances)
nrow(daily_balances)
sum(is.na(daily_balances$account_name))

cat("----------------------\n")
interest_report <- get_interest_report(data_frame)
head(interest_report)
nrow(interest_report)

cat("----------------------\n")
suspicious_report <- get_suspicious_report(data_frame)
head(suspicious_report)
nrow(suspicious_report)

write_csv(suspicious_report, "report3.csv")
# n_distinct(data_frame$account_name, na.rm = TRUE)