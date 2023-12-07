"""https://adventofcode.com/2023/day/1"""


# part 1


def part1(A):
    return sum(int(next(c for c in row if c.isdigit())
                   + next(c for c in row[::-1] if c.isdigit()))
               for row in A)


# part 2 string search
# string search in general is N^3 time brute force hash compare, N^2 time using naive trie, N time using aho-corasick.
# brute force is good:
#   - only need to search each line
#   - only need to search before first number character and after last number character
#   - only need to search for 3,4,5 character words
#   - lines are short, document is small


NUMS = {
    'one': '1',
    'two': '2',
    'three': '3',
    'four': '4',
    'five': '5',
    'six': '6',
    'seven': '7',
    'eight': '8',
    'nine': '9'
}


def find_num_left(S):
    """
    Return the first number from the left in S.
    Can be number character or converted number word.
    """
    i, c = next(((i, c) for i, c in enumerate(S) if c.isdigit()),
                (len(S), 'NOTFOUND'))
    for j in range(i):
        for k in range(j+3, j+6):  # shortest word is 3 chars, longest is 5
            word = S[j:k]
            if word in NUMS:
                return NUMS[word]
    return c


def find_num_right(S):
    """
    Return the first number from the right in S.
    Can be number character or converted number word.
    """
    i, c = next(((i, c) for i, c in list(enumerate(S))[::-1] if c.isdigit()),
                (0, 'NOTFOUND'))
    for j in range(len(S)-3, i-1, -1):
        for k in range(j+3, j+6):  # shortest word is 3 chars, longest is 5
            word = S[j:k]
            if word in NUMS:
                return NUMS[word]
    return c


def part2(A):
    return sum(int(find_num_left(row) + find_num_right(row)) for row in A)


IN = open('day01_input.txt').readlines()

TEST = """
1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet
""".strip().splitlines()

print('part1 test:', part1(TEST))
print('part1:', part1(IN))

TEST = """
two1nine
eightwothree
abcone2threexyz
xtwone3four
4nineeightseven2
zoneight234
7pqrstsixteen
""".strip().splitlines()

print('part2 test:', part2(TEST))
print('part2:', part2(IN))
