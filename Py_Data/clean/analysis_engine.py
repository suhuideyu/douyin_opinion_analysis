"""
抖音评论情感分析引擎 — 基于 SnowNLP
"""
from snownlp import SnowNLP


class SentimentAnalyzer:

    def analyze(self, text):
        if not text or not text.strip():
            return 0
        try:
            score = SnowNLP(text.strip()).sentiments
        except Exception:
            return 0
        if score > 0.6:
            return 1
        elif score < 0.4:
            return -1
        return 0

    def analyze_batch(self, texts):
        return [self.analyze(t) for t in texts]


_analyzer = None


def get_analyzer():
    global _analyzer
    if _analyzer is None:
        _analyzer = SentimentAnalyzer()
    return _analyzer


if __name__ == '__main__':
    analyzer = get_analyzer()
    tests = [
        ("好可爱的小猫咪！！！", 1),
        ("真的太难看了想吐", -1),
        ("今天天气不错", 0),
        ("虽然可爱但是太贵了", 1),
        ("笑死我了哈哈哈哈太搞笑了", 1),
        ("一般般吧没意思", -1),
        ("不好看", -1),
        ("超级好看", 1),
        ("非常难看", -1),
        ("一般般,就那样", 0),
    ]
    for text, expected in tests:
        result = analyzer.analyze(text)
        status = "PASS" if result == expected else "FAIL(exp:" + str(expected) + ")"
        print(f"  {status} [{result}] {text}")
