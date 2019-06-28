/*
 FusionCharts JavaScript Library - Tree Map Chart
 Copyright FusionCharts Technologies LLP
 License Information at <http://www.fusioncharts.com/license>

 @version 3.12.2
*/
(function (T) {
    "object" === typeof module && "undefined" !== typeof module.exports ? module.exports = T : T(FusionCharts)
})(function (T) {
    T.register("module", ["private", "modules.renderer.js-treemap",
        function () {
            function Z(a) {
                return a ? a.replace(/^#*/, "#") : "#E5E5E5"
            }

            function r(a, b, d) {
                this.label = a;
                this.value = parseFloat(b, 10);
                this.colorValue = parseFloat(d, 10);
                this.prev = this.next = void 0;
                this.meta = {}
            }

            function O() {
                this._b = [];
                this._css = void 0;
                this.rangeOurEffectApplyFn = function () {};
                this.statePointerLow = {
                    value: void 0,
                    index: void 0
                };
                this.statePointerHigh = {
                    value: void 0,
                    index: void 0
                }
            }
            var ia, ja, ga, ka, D = this.hcLib,
                aa = D.chartAPI,
                N = Math,
                W = N.max,
                la = N.round,
                sa = N.tan,
                ma = N.min,
                ta = N.PI,
                na = D.extend2,
                X = this.window,
                ua = D.parsexAxisStyles,
                N = D.Raphael,
                oa = D.graphics,
                ba = oa.convertColor,
                pa = oa.getLightColor,
                K = this.raiseEvent,
                y = D.pluckNumber,
                H = D.pluck,
                va = D.each,
                ca = D.BLANKSTRING,
                wa = "rgba(192,192,192," + (/msie/i.test(X.navigator.userAgent) && !X.opera ? .002 : 1E-6) + ")",
                X = !D.CREDIT_REGEX.test(X.location.hostname),
                qa = D.plotEventHandler,
                da = D.schedular,
                xa = D.preDefStr.DEFAULT;
            N.addSymbol({
                backIcon: function (a, b, d) {
                    --d;
                    var c = b + d,
                        t = c - d / 2,
                        k = a + d,
                        n = t - d;
                    return ["M", a, b - d, "L", a - d, b, a, c, a, t, k, t, k, n, k - d, n, "Z"]
                }, homeIcon: function (a, b, d) {
                    --d;
                    var c = 2 * d,
                        t = a - d,
                        k = t + c / 6,
                        n = b + d,
                        B = k + c / 4,
                        w = n - d / 2,
                        z = B + c / 6,
                        m = w + d / 2,
                        A = z + c / 4,
                        f = m - d;
                    return ["M", a, b - d, "L", t, b, k, b, k, n, B, n, B, w, z, w, z, m, A, m, A, f, A + c / 6, f, "Z"]
                }
            });
            r.prototype.constructor = r;
            r.prototype.getCSSconf = function () {
                return this.cssConf
            };
            r.prototype.getPath = function () {
                return this.path
            };
            r.prototype.setPath = function () {
                var a = this.getParent();
                this.path = (a ?
                    a.getPath() : []).concat(this)
            };
            r.prototype.addChild = function (a) {
                a instanceof r && (this.next = this.next || [], [].push.call(this.next, a), a.setParent(this));
                return this.next
            };
            r.prototype.getChildren = function () {
                return this.next
            };
            r.prototype.addChildren = function (a, b) {
                var d = this.getChildren() || (this.next = []),
                    c = d.length;
                b || (b = c - 1);
                d.splice(b > c - 1 ? c - 1 : 0 > b ? 0 : b, 0, a);
                a.setParent(this)
            };
            r.prototype.getDepth = function () {
                return this.meta.depth
            };
            r.prototype.isLeaf = function (a) {
                return (a ? this.getDepth() < a : !0) && this.next
            };
            r.prototype.setParent = function (a) {
                a instanceof r && (this.prev = a);
                return this
            };
            r.prototype.getSiblingCount = function (a) {
                var b, d = 0,
                    c = this;
                if (this instanceof r) {
                    b = this.getParent();
                    if (a) {
                        for (; c;)(c = c.getSibling(a)) && (d += 1);
                        return d
                    }
                    if (b) return b.getChildren().length
                }
            };
            r.prototype.getParent = function () {
                return this.prev
            };
            r.prototype.getLabel = function () {
                return this.label
            };
            r.prototype.getValue = function () {
                return this.value
            };
            r.prototype.setValue = function (a, b) {
                this.value = b ? this.value + a : a
            };
            r.prototype.getColorValue =
                function () {
                    return this.colorValue
                };
            r.prototype.getSibling = function (a) {
                a = a.toLowerCase();
                var b = this.getParent(),
                    d = this.getLabel(),
                    c, t;
                if (b)
                    for (b = b.getChildren(), c = 0; c < b.length; c++)
                        if (t = b[c], t = t.getLabel(), t === d) switch (a) {
                        case "left":
                            return b[c - 1];
                        case "right":
                            return b[c + 1]
                        }
            };
            r.prototype.setMeta = function (a, b) {
                this.meta[a] = b
            };
            r.prototype.setDepth = function (a) {
                this.meta.depth = a
            };
            r.prototype.getMeta = function (a) {
                return a ? this.meta[a] : this.meta
            };
            O.prototype.constructor = O;
            O.prototype.resetPointers = function () {
                this.statePointerLow = {
                    value: void 0,
                    index: void 0
                };
                this.statePointerHigh = {
                    value: void 0,
                    index: void 0
                }
            };
            O.prototype.setRangeOutEffect = function (a, b) {
                this._css = a;
                this.rangeOurEffectApplyFn = b
            };
            O.prototype.addInBucket = function (a) {
                var b = this._b,
                    d = a.getColorValue(),
                    c = 0,
                    t = b.length - 1;
                if (d) {
                    a: {
                        for (var k, n; c <= t;)
                            if (k = (c + t) / 2 | 0, n = b[k], n = n.getColorValue(), n < d) c = k + 1;
                            else if (n > d) t = k - 1;
                        else {
                            d = k;
                            break a
                        }
                        d = ~t
                    }
                    b.splice(Math.abs(d), 0, a)
                }
            };
            O.prototype.moveLowerShadePointer = function (a) {
                var b = this._b,
                    d, c, t, k = this.statePointerLow;
                d = k.index;
                c = k.value;
                var n = !1;
                d = void 0 !== d ? d : 0;
                c = void 0 !== c ? c : Number.NEGATIVE_INFINITY;
                if (a !== c) {
                    if (c <= a) {
                        for (;;) {
                            t = (c = b[d++]) ? c.getColorValue() : 0;
                            if (a < t || !c) break;
                            n = !0;
                            c.rangeOutEffect = this._css;
                            this.rangeOurEffectApplyFn.call(c, this._css)
                        }
                        d = n ? d - 2 : d - 1
                    } else {
                        for (;;) {
                            t = (c = b[d--]) ? c.getColorValue() : 0;
                            if (a >= t || !c) break;
                            c.cssConf = c.cssConf || {};
                            n = !0;
                            delete c.rangeOutEffect;
                            c.cssConf.opacity = 1;
                            this.rangeOurEffectApplyFn.call(c, c.cssConf)
                        }
                        d = n ? d + 2 : d + 1
                    }
                    k.index = d;
                    k.value = a
                }
            };
            O.prototype.moveHigherShadePointer = function (a) {
                var b =
                    this._b,
                    d = b.length,
                    c, t, k = this.statePointerHigh;
                t = k.index;
                c = k.value;
                var n = !1,
                    d = void 0 !== t ? t : d - 1;
                c = void 0 !== c ? c : Number.POSITIVE_INFINITY;
                if (a !== c) {
                    if (c > a) {
                        for (;;) {
                            t = (c = b[d--]) ? c.getColorValue() : 0;
                            if (a >= t || !c) break;
                            n = !0;
                            c.rangeOutEffect = this._css;
                            this.rangeOurEffectApplyFn.call(c, this._css)
                        }
                        d = n ? d + 2 : d + 1
                    } else {
                        for (;;) {
                            t = (c = b[d++]) ? c.getColorValue() : 0;
                            if (a < t || !c) break;
                            c.cssConf = c.cssConf || {};
                            n = !0;
                            delete c.rangeOutEffect;
                            c.cssConf.opacity = 1;
                            this.rangeOurEffectApplyFn.call(c, c.cssConf)
                        }
                        d = n ? d - 2 : d - 1
                    }
                    k.index =
                        d;
                    k.value = a
                }
            };
            aa("treemap", {
                friendlyName: "TreeMap",
                standaloneInit: !0,
                hasGradientLegend: !0,
                creditLabel: X,
                defaultDatasetType: "treemap",
                enableMouseOutEvent: !0,
                applicableDSList: {
                    treemap: !0
                },
                _mouseEvtHandler: function (a) {
                    var b = a.data,
                        d = b.chart,
                        b = b.mouseTracker,
                        c = d.config,
                        t = c.canvasLeft,
                        k = c.canvasRight,
                        n = c.canvasBottom,
                        B = c.canvasTop,
                        w = c.datasetOrder || d.components.dataset,
                        c = D.getMouseCoordinate(d.linkedItems.container, a.originalEvent, d),
                        z = c.chartX,
                        m = c.chartY,
                        A, f, h = !1,
                        l = w.length,
                        x, v = b._lastDatasetIndex,
                        c =
                        b._lastPointIndex;
                    if (z > t && z < k && m > B && m < n || d.config.plotOverFlow)
                        for (; l-- && !h;)(A = w[l]) && (f = A._getHoveredPlot && A._getHoveredPlot(z, m)) && f.hovered && (h = !0, f.datasetIndex = l, x = b._getMouseEvents(a, f.datasetIndex, f.pointIndex));
                    (!h || x && x.fireOut) && void 0 !== v && (delete b._lastDatasetIndex, delete b._lastPointIndex, w[v] && w[v]._firePlotEvent && w[v]._firePlotEvent("mouseout", c, a));
                    if (h)
                        for (d = x.events && x.events.length, b._lastDatasetIndex = f.datasetIndex, c = b._lastPointIndex = f.pointIndex, f = 0; f < d; f += 1) A && A._firePlotEvent &&
                            A._firePlotEvent(x.events[f], c, a)
                }, addData: function () {
                    var a = this._ref.algorithmFactory,
                        b = Array.prototype.slice.call(arguments, 0);
                    b.unshift("addData");
                    b.unshift(this._getCleanValue());
                    a.realTimeUpdate.apply(this, b)
                }, removeData: function () {
                    var a = this._ref.algorithmFactory,
                        b = Array.prototype.slice.call(arguments, 0);
                    b.unshift("deleteData");
                    b.unshift(this._getCleanValue());
                    a.realTimeUpdate.apply(this, b)
                }, _createToolBox: function () {
                    var a, b, d, c, t = this.components;
                    a = t.chartMenuBar;
                    b = t.actionBar;
                    a && a.drawn || b &&
                        b.drawn || (aa.mscartesian._createToolBox.call(this), a = t.tb, b = a.getAPIInstances(a.ALIGNMENT_HORIZONTAL), d = b.Symbol, b = (t.chartMenuBar || t.actionBar).componentGroups[0], c = new d("backIcon", !1, (a.idCount = a.idCount || 0, a.idCount++), a.pId), a = new d("homeIcon", !1, a.idCount++, a.pId), b.addSymbol(a, !0), b.addSymbol(c, !0), t.toolbarBtns = {
                            back: c,
                            home: a
                        })
                }, _getCleanValue: function () {
                    var a = this.components.numberFormatter;
                    return function (b) {
                        return a.getCleanValue(b)
                    }
                }, _createDatasets: function () {
                    var a = this.components,
                        b =
                        this.jsonData,
                        d = b.dataset,
                        d = b.data || d && d[0].data,
                        c = this.defaultDatasetType,
                        t = [];
                    d && d.length || this.setChartMessage();
                    va(d, function (c) {
                        c.vline || t.push(c)
                    });
                    b = {
                        data: t
                    };
                    this.config.categories = t;
                    a = a.dataset || (a.dataset = []);
                    d ? c && (d = T.get("component", ["dataset", c])) && (a[0] ? (a[0].JSONData = t[0], a[0].configure()) : (this._dsInstance = d = new d, a.push(d), d.chart = this, d.init(b))) : this.setChartMessage()
                }, init: function () {
                    var a = {},
                        b = {},
                        d = {};
                    this._ref = {
                        afAPI: ia(a, b, d),
                        algorithmFactory: ja(a, b, d),
                        containerManager: ka(a,
                            b, d),
                        treeOpt: ga
                    };
                    aa.guageBase.init.apply(this, arguments)
                }
            }, aa.guageBase, {
                enablemousetracking: !0
            });
            T.register("component", ["dataset", "TreeMap", {
                type: "treemap",
                pIndex: 2,
                customConfigFn: "_createDatasets",
                init: function (a) {
                    this.JSONData = a.data[0];
                    this.components = {};
                    this.conf = {};
                    this.graphics = {
                        elemStore: {
                            rect: [],
                            label: [],
                            highlight: [],
                            hot: [],
                            polypath: []
                        }
                    };
                    this.configure()
                }, configure: function () {
                    var a, b = this.chart,
                        d = b.components,
                        c = this.conf,
                        b = b.jsonData.chart;
                    c.metaTreeInf = {};
                    c.algorithm = (b.algorithm || "squarified").toLowerCase();
                    c.horizontalPadding = y(b.horizontalpadding, 5);
                    c.horizontalPadding = 0 > c.horizontalPadding ? 0 : c.horizontalPadding;
                    c.verticalPadding = y(b.verticalpadding, 5);
                    c.verticalPadding = 0 > c.verticalPadding ? 0 : c.verticalPadding;
                    c.showParent = y(b.showparent, 1);
                    c.showChildLabels = y(b.showchildlabels, 0);
                    c.highlightParentsOnHover = y(b.highlightparentsonhover, 0);
                    c.defaultParentBGColor = H(b.defaultparentbgcolor, void 0);
                    c.defaultNavigationBarBGColor = H(b.defaultnavigationbarbgcolor, c.defaultParentBGColor);
                    c.showTooltip = y(b.showtooltip,
                        1);
                    c.baseFontSize = y(b.basefontsize, 10);
                    c.baseFontSize = 1 > c.baseFontSize ? 1 : c.baseFontSize;
                    c.labelFontSize = y(b.labelfontsize, void 0);
                    c.labelFontSize = 1 > c.labelFontSize ? 1 : c.labelFontSize;
                    c.baseFont = H(b.basefont, "Verdana, Sans");
                    c.labelFont = H(b.labelfont, void 0);
                    c.baseFontColor = H(b.basefontcolor, "#000000").replace(/^#?([a-f0-9]+)/ig, "#$1");
                    c.labelFontColor = H(b.labelfontcolor, void 0);
                    c.labelFontColor && (c.labelFontColor = c.labelFontColor.replace(/^#?([a-f0-9]+)/ig, "#$1"));
                    c.labelFontBold = y(b.labelfontbold,
                        0);
                    c.labelFontItalic = y(b.labelfontitalic, 0);
                    c.plotBorderThickness = y(b.plotborderthickness, 1);
                    c.plotBorderThickness = 0 > c.plotBorderThickness ? 0 : 5 < c.plotBorderThickness ? 5 : c.plotBorderThickness;
                    c.plotBorderColor = H(b.plotbordercolor, "#000000").replace(/^#?([a-f0-9]+)/ig, "#$1");
                    c.tooltipSeparationCharacter = H(b.tooltipsepchar, ",");
                    c.plotToolText = H(b.plottooltext, "");
                    c.parentLabelLineHeight = y(b.parentlabellineheight, 12);
                    c.parentLabelLineHeight = 0 > c.parentLabelLineHeight ? 0 : c.parentLabelLineHeight;
                    c.labelGlow =
                        y(b.labelglow, 1);
                    c.labelGlowIntensity = y(b.labelglowintensity, 100) / 100;
                    c.labelGlowIntensity = 0 > c.labelGlowIntensity ? 0 : 1 < c.labelGlowIntensity ? 1 : c.labelGlowIntensity;
                    c.labelGlowColor = H(b.labelglowcolor, "#ffffff").replace(/^#?([a-f0-9]+)/ig, "#$1");
                    c.labelGlowRadius = y(b.labelglowradius, 2);
                    c.labelGlowRadius = 0 > c.labelGlowRadius ? 0 : 10 < c.labelGlowRadius ? 10 : c.labelGlowRadius;
                    c.btnResetChartTooltext = H(b.btnresetcharttooltext, "Back to Top");
                    c.btnBackChartTooltext = H(b.btnbackcharttooltext, "Back to Parent");
                    c.rangeOutBgColor =
                        H(b.rangeoutbgcolor, "#808080").replace(/^#?([a-f0-9]+)/ig, "#$1");
                    c.rangeOutBgAlpha = y(b.rangeoutbgalpha, 100);
                    c.rangeOutBgAlpha = 1 > c.rangeOutBgAlpha || 100 < c.rangeOutBgAlpha ? 100 : c.rangeOutBgAlpha;
                    a = y(b.maxdepth);
                    c.maxDepth = void 0 !== a ? W(a, 1) : void 0;
                    a = c.showNavigationBar = y(b.shownavigationbar, 1);
                    c.slicingMode = H(b.slicingmode, "alternate");
                    c.navigationBarHeight = y(b.navigationbarheight);
                    c.navigationBarHeightRatio = y(b.navigationbarheightratio);
                    c.navigationBarBorderColor = H(b.navigationbarbordercolor, c.plotBorderColor).replace(/^#?([a-f0-9]+)/ig,
                        "#$1");
                    c.navigationBarBorderThickness = a ? y(b.navigationbarborderthickness, c.plotBorderThickness) : 0;
                    c.seperatorAngle = y(b.seperatorangle) * (ta / 180);
                    d.postLegendInitFn({
                        min: 0,
                        max: 100
                    });
                    c.isConfigured = !0
                }, _getHoveredPlot: function (a, b) {
                    var d, c;
                    for (c = (this.kdTree || []).length; c-- && (!this.kdTree[c] || !(d = this.kdTree[c].searchTreemap(a, b))););
                    if (d) return this.pointObj = d, {
                        pointIndex: d.i || d.index,
                        hovered: !0,
                        pointObj: d
                    }
                }, kdTreeAbs: function (a) {
                    function b(c, a, b) {
                        return c >= a && c <= b
                    }

                    function d(a, b, p, u) {
                        var e = {},
                            g;
                        g =
                            u ? "y" : "x";
                        if (0 !== a.length) {
                            if (b === p) return e.point = a[b], e;
                            if (1 === p - b) return a[b][g] > a[p][g] ? (e.point = a[b], e.left = {
                                point: a[p]
                            }) : (e.point = a[p], e.left = {
                                point: a[b]
                            }), e;
                            g = b + p >> 1;
                            u ? t(a, g, b, p) : c(a, g, b, p);
                            e.point = a[g];
                            e.left = d(a, b, g - 1, !u);
                            e.right = d(a, g + 1, p, !u);
                            return e
                        }
                    }

                    function c(a, b, p, d) {
                        for (var e, g, q, C, l; d > p;) {
                            600 < d - p && (e = d - p + 1, g = b - p + 1, q = f(e), C = .5 * h(2 * q / 3), l = .5 * m(q * C * (e - C) / e) * (0 > g - e / 2 ? -1 : 1), q = w(p, z(b - g * C / e + l)), e = A(d, z(b + (e - g) * C / e + l)), c(a, b, q, e));
                            e = a[b];
                            g = p;
                            C = d;
                            k(a, p, b);
                            for (a[d].x > e.x && k(a, p, d); g < C;) {
                                k(a,
                                    g, C);
                                g++;
                                for (C--; a[g].x < e.x;) g++;
                                for (; a[C].x > e.x;) C--
                            }
                            a[p].x === e.x ? k(a, p, C) : (C++, k(a, C, d));
                            C <= b && (p = C + 1);
                            b <= C && (d = C - 1)
                        }
                    }

                    function t(a, b, c, d) {
                        for (var e, g, q, l, R; d > c;) {
                            600 < d - c && (e = d - c + 1, g = b - c + 1, q = f(e), l = .5 * h(2 * q / 3), R = .5 * m(q * l * (e - l) / e) * (0 > g - e / 2 ? -1 : 1), q = w(c, z(b - g * l / e + R)), e = A(d, z(b + (e - g) * l / e + R)), t(a, b, q, e));
                            e = a[b];
                            g = c;
                            l = d;
                            k(a, c, b);
                            for (a[d].y > e.y && k(a, c, d); g < l;) {
                                k(a, g, l);
                                g++;
                                for (l--; a[g].y < e.y;) g++;
                                for (; a[l].y > e.y;) l--
                            }
                            a[c].y === e.y ? k(a, c, l) : (l++, k(a, l, d));
                            l <= b && (c = l + 1);
                            b <= l && (d = l - 1)
                        }
                    }

                    function k(a, c, b) {
                        var d =
                            a[c];
                        a[c] = a[b];
                        a[b] = d
                    }
                    var n = a && a[0] && a[0].plotDetails.rect || 5,
                        B, w = Math.max,
                        z = Math.floor,
                        m = Math.sqrt,
                        A = Math.min,
                        f = Math.log,
                        h = Math.exp,
                        l = Math.pow;
                    B = {};
                    a = a || [];
                    for (B = a.length; B--;) a[B].r > n && (n = a[B].r), a[B].x = +a[B].plotDetails.rect.x, a[B].y = +a[B].plotDetails.rect.y;
                    B = {
                        tree: d(a, 0, a.length - 1, !1),
                        search: function (a, c) {
                            function d(e) {
                                var p = b(a, e.x1, e.x2) && b(c, e.y1, e.y2),
                                    q;
                                q = e.point.y;
                                q = m(l(a - e.point.x, 2) + l(c - q, 2));
                                g ? p ? w ? e.point.i > g.point.i && (g = e, w = p, A = q) : (g = e, w = p, A = q) : !w && q < A && (g = e, w = p, A = q) : (g = e, w = p, A = q)
                            }

                            function u(a) {
                                a &&
                                    a.point && (b(a.point.x, q, f) && b(a.point.y, h, z) && d(a), q <= a.point.x && e(a.left), f >= a.point.x && e(a.right))
                            }

                            function e(a) {
                                a && a.point && (b(a.point.x, q, f) && b(a.point.y, h, z) && d(a), h <= a.point.y && u(a.left), z >= a.point.y && u(a.right))
                            }
                            var g, q = a - n,
                                f = a + n,
                                h = c - n,
                                z = c + n,
                                w = !1,
                                A = 0;
                            u(this.tree);
                            return g && g.point || g
                        }, searchTreemap: function (a, c) {
                            var b, d = function (e, g) {
                                if (e && e.point) {
                                    var q = e.point.x,
                                        l = q + e.point.plotDetails.rect.width,
                                        f = e.point.y,
                                        h = f + e.point.plotDetails.rect.height;
                                    e.point.x2 = l;
                                    e.point.y2 = h;
                                    a >= q && a <= l && c >= f &&
                                        c <= h && (q = e.point, b ? q.i > b.i && (b = q) : b = q);
                                    d(e.left, !g);
                                    d(e.right, !g)
                                }
                            };
                            d(this.tree, !1);
                            return b
                        }
                    };
                    a.sort(function (a, c) {
                        return a.i - c.i
                    });
                    return B
                }, kdTreePartioning: function () {
                    var a = this.chart.config.trackerConfig,
                        b, d = [];
                    for (b = a.length; b--;) a[b].i = b, void 0 === d[a[b].node.meta.depth] && (d[a[b].node.meta.depth] = []), d[a[b].node.meta.depth].push(a[b]);
                    this.kdTree = [];
                    for (b = d.length; b--;) this.kdTree[b] = this.kdTreeAbs && this.kdTreeAbs(d[b])
                }, _rolloverResponseSetter: function (a, b, d) {
                    var c = b.getData() || {};
                    b && (b.attr(c.setRolloverAttr),
                        qa.call(b, a, d, "DataPlotRollOver"))
                }, _rolloutResponseSetter: function (a, b, d) {
                    var c = b.getData() || {};
                    b && (b.attr(c.setRolloutAttr), qa.call(b, a, d, "DataPlotRollOut"))
                }, _firePlotEvent: function (a, b, d) {
                    var c = this,
                        t = c.conf,
                        k = c.chart,
                        n = k.getJobList(),
                        B = k.components.paper;
                    b = c.chart.config.trackerConfig[b || 0];
                    var w = k.graphics.trackerGroup,
                        z = b && b.node && b.node.plotItem,
                        m = b && b.evtFns && b.evtFns.tooltip[0],
                        A = k.components.paper.canvas.style,
                        f = D.toolTip;
                    d = d.originalEvent;
                    var h = c.graphics.singleTracker,
                        l = c.pointObj.plotDetails,
                        x = l && l.rect,
                        t = t.highlightParentsOnHover,
                        v = b && b.node.path,
                        p = (v = v && v[v.length - 2]) && v.rect;
                    h || (h = c.graphics.singleTracker = B.rect(w));
                    z ? t && v ? h.attr({
                        x: p.x,
                        y: p.y,
                        width: p.width,
                        height: p.height,
                        stroke: "rgba(255,255,255,0)"
                    }) : (l ? h.attr({
                        x: x.x || 0,
                        y: x.y || 0,
                        width: x.width || 0,
                        height: x.height || 0,
                        stroke: "rgba(255,255,255,0)"
                    }) : h.attr({
                        x: 0,
                        y: 0,
                        width: 0,
                        height: 0,
                        stroke: "rgba(255,255,255,0)"
                    }), h.toFront()) : (z = b && b.node.dirtyNode.plotItem, l = {}, x = {});
                    if (z) switch (a) {
                    case "mouseover":
                        b.evtFns.hover[0](h);
                        m && (f.setStyle(B),
                            f.setPosition(d), f.draw(m, B));
                        A.cursor = "pointer";
                        break;
                    case "mouseout":
                        h.attr({
                            x: 0,
                            y: 0,
                            width: 0,
                            height: 0,
                            stroke: "#ffffff",
                            "stroke-width": "0px"
                        });
                        f.hide();
                        A.cursor = xa;
                        h.toFront();
                        c._rolloutResponseSetter(k, z, d);
                        break;
                    case "click":
                        b && b.evtFns && b.evtFns.click && b.evtFns.click[0]();
                        m && (f.setStyle(B), f.setPosition(d), f.draw(m, B));
                        n.trackerDrawID.push(da.addJob(function () {
                            c.kdTreePartioning()
                        }, D.priorityList.tracker));
                        break;
                    case "mousemove":
                        m && (f.setPosition(d), f.draw(m, B))
                    }
                }, draw: function () {
                    var a = this,
                        b =
                        a.conf,
                        d = a.chart,
                        c = d.config.trackerConfig,
                        t = d.getJobList(),
                        k = d.config,
                        n = d.components,
                        B = k.canvasLeft,
                        w = k.canvasRight,
                        z = k.canvasBottom,
                        m = k.canvasTop,
                        A = n.paper,
                        f = d.jsonData.chart,
                        h = d.graphics,
                        l = h.trackerGroup,
                        x, v, p, u, e, k = b.metaTreeInf,
                        g = a.graphics.elemStore,
                        q = {},
                        C, R, I = ["fontFamily", "fontSize", "fontWeight", "fontStyle"],
                        ra = {},
                        P, n = n.gradientLegend,
                        r, Q = d._ref,
                        Y = Q.afAPI.visibilityController,
                        J = d.get("config", "animationObj"),
                        F = J.duration || 0,
                        ea = J.dummyObj,
                        y = J.animObj,
                        fa = J.animType,
                        S, J = Q.containerManager,
                        Q = Q.algorithmFactory,
                        G;
                    c && (c.length = 0);
                    S = ua({}, {}, f, {
                        fontFamily: "Verdana,sans",
                        fontSize: "10px"
                    });
                    c = 0;
                    for (f = I.length; c < f; c++) G = I[c], G in S && (ra[G] = S[G]);
                    for (v in g) {
                        I = g[v];
                        c = 0;
                        for (f = I.length; c < f; c++)(S = I[c]) && S.remove && S.remove();
                        I.length = 0
                    }
                    J.remove();
                    x = h.datasetGroup = h.datasetGroup || A.group("dataset");
                    v = h.datalabelsGroup = (h.datalabelsGroup || A.group("datalabels").insertAfter(x)).css(ra);
                    p = h.lineHot = h.lineHot || A.group("line-hot", l);
                    u = h.labelHighlight = h.labelHighlight || A.group("labelhighlight", v);
                    e = h.floatLabel = h.floatLabel ||
                        A.group("labelfloat", v).insertAfter(u);
                    b.colorRange = n.colorRange;
                    k.effectiveWidth = w - B;
                    k.effectiveHeight = z - m;
                    k.startX = B;
                    k.startY = m;
                    C = k.effectiveWidth / 2;
                    R = k.effectiveHeight / 2;
                    C = k.effectiveWidth / 2;
                    R = k.effectiveHeight / 2;
                    q.drawPolyPath = function (a, c) {
                        var b, e;
                        b = (q.graphicPool(!1, "polyPathItem") || (e = A.path(x))).attr({
                            path: a._path
                        }).animateWith(ea, y, {
                            path: a.path
                        }, F, fa);
                        b.css(c);
                        e && g.polypath.push(e);
                        return b
                    };
                    q.drawRect = function (a, c, b, e) {
                        var d, p, l = {},
                            v = {},
                            f;
                        for (d in a) p = a[d], 0 > p && (a[d] = 0, v.visibility = "hidden");
                        na(l, a);
                        l.x = C;
                        l.y = R;
                        l.height = 0;
                        l.width = 0;
                        P = q.graphicPool(!1, "plotItem") || (f = A.rect(x));
                        P.attr(b && (b.x || b.y) && b || l);
                        P.attr(e);
                        P.animateWith(ea, y, a, F, fa, Y.controlPostAnimVisibility);
                        P.css(c).toFront();
                        P.css(v);
                        f && g.rect.push(f);
                        return P
                    };
                    q.drawText = function (a, c, d, l, p) {
                        var v = {},
                            x, f, h = q.graphicPool(!1, "labelItem") || (x = A.text(e)),
                            m = q.graphicPool(!1, "highlightItem") || (f = A.text(u)),
                            w = d.textAttrs;
                        d = d.highlightAttrs;
                        na(v, w);
                        delete v.fill;
                        v["stroke-linejoin"] = "round";
                        h.attr({
                            x: l.x || C,
                            y: l.y || R,
                            fill: "#000000"
                        }).css(w);
                        h.attr(p);
                        a = 0 > c.x || 0 > c.y ? ca : a;
                        h.animateWith(ea, y, {
                            text: a,
                            x: c.x,
                            y: c.y
                        }, F, fa);
                        m.attr({
                            text: a,
                            x: l.x || C,
                            y: l.y || R,
                            stroke: b.labelGlow ? "#ffffff" : wa
                        }).css(v).css(d);
                        m.attr(p);
                        m.animateWith(ea, y, {
                            x: c.x,
                            y: c.y
                        }, F, fa);
                        g.label.push(x);
                        g.highlight.push(f);
                        return {
                            label: h,
                            highlightMask: m
                        }
                    };
                    q.drawHot = function (a, c) {
                        var b;
                        b = a.plotItem || {};
                        var e = a.rect,
                            d, l, v;
                        for (l in e) v = e[l], 0 > v && (e[l] = 0);
                        b = b.tracker = A.rect(p).attr(e).attr({
                            cursor: "pointer",
                            fill: "rgba(255, 255, 255, 0)",
                            stroke: "none"
                        });
                        for (d in c) e = c[d], b[d].apply(b,
                            e);
                        g.hot.push(b);
                        return b
                    };
                    q.disposeItems = function (a, c) {
                        var b, e, d, g = c || "plotItem labelItem hotItem highlightItem polyPathItem pathlabelItem pathhighlightItem stackedpolyPathItem stackedpathlabelItem stackedpathhighlightItem".split(" ");
                        for (b = 0; b < g.length; b += 1) d = g[b], (e = a[d]) && q.graphicPool(!0, d, e, a.rect), e && e.hide(), a[d] = void 0
                    };
                    q.disposeChild = function () {
                        var a, b = function () {
                                return a.disposeItems
                            },
                            c = function (a, e) {
                                var d, g;
                                b(a);
                                for (d = 0; d < (a.getChildren() || []).length; d++) g = a.getChildren(), d = c(g[d], d);
                                return e
                            };
                        return function (e) {
                            var d = e.getParent();
                            a || (a = this, b = b());
                            d ? a.disposeChild(d) : c(e, 0)
                        }
                    }();
                    q.graphicPool = function () {
                        var a = {};
                        return function (b, c, e) {
                            var d = a[c];
                            d || (d = a[c] = []);
                            "hotItem" !== c && "pathhotItem" !== c || e.remove();
                            if (b) d.push(e);
                            else if (b = d.splice(0, 1)[0]) return b.show(), b
                        }
                    }();
                    q.disposeComplimentary = function (a) {
                        var b, c;
                        b = a.getParent();
                        var e = a.getSiblingCount("left");
                        b && (c = b.getChildren(), b = c.splice(e, 1)[0], this.disposeChild(a), c.splice(e, 0, b));
                        this.removeLayers()
                    };
                    q.removeLayers = function () {
                        var a,
                            b, c, e;
                        c = g.hot;
                        e = c.length;
                        for (a = 0; a < e; a++)(b = c[a]) && b.remove();
                        c.length = 0
                    };
                    Q.init(b.algorithm, !0, b.maxDepth);
                    d = Q.plotOnCanvas(a.JSONData, void 0, d._getCleanValue());
                    J.init(a, k, q, void 0, d);
                    J.draw();
                    r = Q.applyShadeFiltering({
                        fill: b.rangeOutBgColor,
                        opacity: .01 * b.rangeOutBgAlpha
                    }, function (a) {
                        this.plotItem && this.plotItem.css(a)
                    });
                    n && n.enabled && (n.resetLegend(), n.clearListeners());
                    n.notifyWhenUpdate(function (a, b) {
                        r.call(this, {
                            start: a,
                            end: b
                        })
                    }, this);
                    b.isConfigured = !1;
                    t.trackerDrawID.push(da.addJob(function () {
                            a.kdTreePartioning()
                        },
                        D.priorityList.tracker))
                }
            }]);
            ia = function (a, b, d) {
                function c(a, b, c) {
                    this.node = a;
                    this.bucket = b ? new O : void 0;
                    this.cleansingFn = c
                }
                var t, k, n, B;
                c.prototype.get = function () {
                    var a = this.order,
                        b = this.bucket,
                        c = this.cleansingFn;
                    return function f(d, l) {
                        var x, v, p, u;
                        v = ["label", "value", "data", "svalue"];
                        if (d)
                            for (u in x = new r(d.label, c(d.value), c(d.svalue)), p = d.data || [], 0 === p.length && b && b.addInBucket(x), x.setDepth(l), d) - 1 === v.indexOf(u) && x.setMeta(u, d[u]);
                        a && (p = a(p));
                        for (v = 0; v < p.length; v++) u = p[v], u = f(u, l + 1), x.addChild(u);
                        return x
                    }(this.node, 0)
                };
                c.prototype.getBucket = function () {
                    return this.bucket
                };
                c.prototype.getMaxDepth = function () {
                    return k
                };
                t = function (a, b) {
                    function c(a) {
                        this.iterAPI = a
                    }
                    var d = b && b.exception,
                        f, h;
                    c.prototype.constructor = c;
                    c.prototype.initWith = function (a) {
                        return this.iterAPI(a)
                    };
                    f = (new c(function (a) {
                        var b = a,
                            c = [],
                            p = !1;
                        c.push(b);
                        return {
                            next: function (a) {
                                var b, g;
                                if (!p) {
                                    b = c.shift();
                                    if (d && b === d && (b = c.shift(), !b)) {
                                        p = !0;
                                        return
                                    }(g = (a = void 0 !== a ? b.getDepth() >= a ? [] : b.getChildren() : b.getChildren()) && a.length || 0) && [].unshift.apply(c, a);
                                    0 === c.length && (p = !0);
                                    return b
                                }
                            }, reset: function () {
                                p = !1;
                                b = a;
                                c.length = 0;
                                c.push(b)
                            }
                        }
                    })).initWith(a);
                    h = (new c(function (a) {
                        var b = a,
                            c = [],
                            d = [],
                            f = !1;
                        c.push(b);
                        d.push(b);
                        return {
                            next: function () {
                                var a, b, d;
                                if (!f) return b = c.shift(), (d = (a = b.getChildren()) && a.length || 0) && [].push.apply(c, a), 0 === c.length && (f = !0), b
                            }, nextBatch: function () {
                                var a, b;
                                if (!f) return a = d.shift(), (b = (a = a.getChildren()) && a.length || 0) && [].push.apply(d, a), 0 === c.length && (f = !0), a
                            }, reset: function () {
                                f = !1;
                                b = a;
                                c.length = 0;
                                c.push(b)
                            }
                        }
                    })).initWith(a);
                    return {
                        df: f,
                        bf: h
                    }
                };
                B = function () {
                    function a() {
                        this.con = {}
                    }
                    var b = {},
                        c;
                    a.prototype.constructor = a;
                    a.prototype.get = function (a) {
                        return this.con[a]
                    };
                    a.prototype.set = function (a, b) {
                        this.con[a] = b
                    };
                    a.prototype["delete"] = function (a) {
                        return delete this.con[a]
                    };
                    return {
                        getInstance: function (d) {
                            var f;
                            return (f = b[d]) ? c = f : c = b[d] = new a
                        }
                    }
                }();
                b = function () {
                    var a = [],
                        b, c = !1,
                        d = {
                            visibility: "visible"
                        };
                    return {
                        controlPreAnimVisibility: function (d, h) {
                            var l, x, v;
                            if (d) {
                                for (x = d;;) {
                                    x = x.getParent();
                                    if (!x) break;
                                    l = x
                                }
                                l = t(l, {
                                    exception: d
                                });
                                for (l = l.df;;) {
                                    x = l.next();
                                    if (!x) break;
                                    v = x.overAttr || (x.overAttr = {});
                                    v.visibility = "hidden";
                                    a.push(x)
                                }
                                b = h || d.getParent();
                                c = !1;
                                return a
                            }
                        }, displayAll: function (d) {
                            var h;
                            if (d) {
                                d = t(d.getParent() || d);
                                for (d = d.df;;) {
                                    h = d.next();
                                    if (!h) break;
                                    h = h.overAttr || (h.overAttr = {});
                                    h.visibility = "visible"
                                }
                                b = void 0;
                                a.length = 0;
                                c = !1
                            }
                        }, controlPostAnimVisibility: function () {
                            var f, h;
                            if (!c && (c = !0, b)) {
                                h = t(b);
                                for (h = h.df;;) {
                                    f = h.next(k);
                                    if (!f) break;
                                    if (f = f.dirtyNode) f && f.plotItem.attr(d), (f = f && f.textItem) && f.label && f.label.attr(d), f &&
                                        f.label && f.highlightMask.attr(d)
                                }
                                b = void 0;
                                a.length = 0
                            }
                        }
                    }
                }();
                a.AbstractTreeMaker = c;
                a.iterator = t;
                a.initConfigurationForlabel = function (a, b, c) {
                    var d = a.x,
                        f = a.y,
                        h = b / 2,
                        l = c.showParent ? 0 : 1,
                        x = c.showChildLabels;
                    return function (a, p, u, e) {
                        u = !1;
                        var g = {
                                x: void 0,
                                y: void 0,
                                width: void 0,
                                height: void 0
                            },
                            q = {},
                            C = 0,
                            w = {},
                            I = {},
                            t, w = a.meta;
                        if (a) return a.isLeaf(k) || (u = !0), q.label = a.getLabel(), g.width = p.width - 2 * d, g.x = p.x + p.width / 2, a = p.height - 2 * f, !u && a < b && (g.height = -1), !e && u ? (g.height = x ? g.height ? g.height : p.height - 2 * f : -1, g.y = p.y +
                            p.height / 2) : l ? (g.y = -1, b = f = 0, t = "hidden") : (g.height = g.height ? g.height : b, g.y = p.y + f + h), C += 2 * f, C += b, q.rectShiftY = C, q.textRect = g, c.labelGlow ? (I["stroke-width"] = c.labelGlowRadius, I.opacity = c.labelGlowIntensity, I.stroke = c.labelGlowColor, I.visibility = "hidden" === t ? "hidden" : "visible") : I.visibility = "hidden", w = {
                            fill: w && w.fontcolor && Z(w.fontcolor) || c.labelFontColor || c.baseFontColor,
                            visibility: t
                        }, {
                            conf: q,
                            attr: w,
                            highlight: I
                        }
                    }
                };
                a.context = B;
                a.mapColorManager = function (a, b, c) {
                    var d = Z(c ? a.defaultNavigationBarBGColor :
                        a.defaultParentBGColor);
                    return function (c, h, l) {
                        h = {};
                        var x = c.cssConf,
                            v = c.meta,
                            v = v.fillcolor ? Z(v.fillcolor) : void 0,
                            p = c.getParent(),
                            u;
                        u = c.getColorValue();
                        a.isLegendEnabled = !0;
                        u = a.isLegendEnabled && u === u ? b.getColorByValue(u) && "#" + b.getColorByValue(u) || Z(b.rangeOutsideColor) : void 0;
                        c.isLeaf(k) ? h.fill = v || u || d : (c = (c = (p ? p : c).cssConf) && c.fill, h.fill = v || (u ? u : c));
                        h.stroke = l ? a.navigationBarBorderColor : a.plotBorderColor;
                        h.strokeWidth = l ? a.navigationBarBorderThickness : a.plotBorderThickness;
                        h["stroke-dasharray"] =
                            "none";
                        !l && x && "--" === x["stroke-dasharray"] && (h["stroke-dasharray"] = x["stroke-dasharray"], h.strokeWidth = x.strokeWidth);
                        return h
                    }
                };
                a.abstractEventRegisterer = function (c, b, k, t) {
                    function f(a) {
                        var c = {},
                            b, d;
                        for (b in y) d = y[b], c[d] = a[b];
                        return c
                    }
                    var h = b.chart,
                        l = h.components,
                        x = l.dataset[0],
                        v = l.toolbarBtns,
                        p = h.chartInstance,
                        u = b.conf,
                        e = l.gradientLegend,
                        g = c.drawTree,
                        q = t.disposeChild,
                        C, R = h.getJobList(),
                        I = arguments,
                        B, P, y = {
                            colorValue: "svalue",
                            label: "name",
                            value: "value",
                            rect: "metrics"
                        };
                    B = a.context.getInstance("ClickedState");
                    h._intSR = {};
                    h._intSR.backToParent = C = function (b) {
                        var c = this,
                            d = c,
                            e = d && c.getParent(),
                            l = a.context.getInstance("ClickedState").get("VisibileRoot") || {};
                        h.config.trackerConfig.length = 0;
                        R.trackerDrawID.push(da.addJob(function () {
                            x.kdTreePartioning()
                        }, D.priorityList.tracker));
                        b ? K("beforedrillup", {
                            node: c,
                            withoutHead: !u.showParent
                        }, p, void 0, function () {
                            e && (l.state = "drillup", l.node = [{
                                    virginNode: a.getVisibleRoot()
                                },
                                e
                            ], q(d), g.apply(e, I));
                            K("drillup", {
                                node: c,
                                withoutHead: !u.showParent,
                                drillUp: C,
                                drillUpToTop: P
                            }, p);
                            c = c && c.getParent()
                        }, function () {
                            K("drillupcancelled", {
                                node: c,
                                withoutHead: !u.showParent
                            }, p)
                        }) : (e && (l.state = "drillup", l.node = [{
                                virginNode: a.getVisibleRoot()
                            },
                            e
                        ], q(d), g.apply(e, I)), c = c && c.getParent())
                    };
                    h._intSR.resetTree = P = function (c) {
                        var b = this,
                            d = b && b.getParent(),
                            e, l = a.context.getInstance("ClickedState").get("VisibileRoot") || {};
                        h.config.trackerConfig.length = 0;
                        for (R.trackerDrawID.push(da.addJob(x.kdTreePartioning, D.priorityList.tracker)); d;) e = d, d = d.getParent();
                        c ? K("beforedrillup", {
                                node: b,
                                withoutHead: !u.showParent
                            },
                            p, void 0, function () {
                                e && (l.state = "drillup", l.node = [{
                                        virginNode: a.getVisibleRoot()
                                    },
                                    e
                                ], q(e), g.apply(e, I), K("drillup", {
                                    node: b,
                                    sender: h.fusionCharts,
                                    withoutHead: !u.showParent,
                                    drillUp: C,
                                    drillUpToTop: P
                                }, p))
                            }, function () {
                                K("drillupcancelled", {
                                    node: b,
                                    withoutHead: !u.showParent
                                }, p)
                            }) : e && (l.state = "drillup", l.node = [{
                                virginNode: a.getVisibleRoot()
                            },
                            e
                        ], q(e), g.apply(e, I))
                    };
                    return {
                        click: function (a, b) {
                            var g = a.virginNode,
                                l = x.graphics.singleTracker,
                                k = x.kdTree,
                                m, I, z;
                            K("dataplotclick", f(a.virginNode), p);
                            if (I = g.getParent()) {
                                if (g ===
                                    b) z = I, k && (k.length = 0), h.config.trackerConfig.length = 0, m = "drillup";
                                else {
                                    if (g.next) z = g, k && (k.length = 0);
                                    else if (z = I, b === z) {
                                        m = void 0;
                                        return
                                    }
                                    h.config.trackerConfig.length = 0;
                                    m = "drilldown"
                                }
                                e && e.enabled && e.resetLegend();
                                c.applyShadeFiltering.reset();
                                m && K("before" + m, {
                                    node: z,
                                    withoutHead: !u.showParent
                                }, p, void 0, function () {
                                    B.set("VisibileRoot", {
                                        node: a,
                                        state: m
                                    });
                                    q.call(t, z);
                                    n = z;
                                    d.draw();
                                    K(m, {
                                        node: z,
                                        withoutHead: !u.showParent,
                                        drillUp: C,
                                        drillUpToTop: P
                                    }, p)
                                }, function () {
                                    K(m + "cancelled", {
                                            node: z,
                                            withoutHead: !u.showParent
                                        },
                                        p)
                                });
                                v.back && v.back.attachEventHandlers({
                                    click: C.bind(z)
                                });
                                v.home && v.home.attachEventHandlers({
                                    click: P.bind(z)
                                });
                                l && l.attr({
                                    x: 0,
                                    y: 0,
                                    width: 0,
                                    height: 0,
                                    stroke: "rgba(255,255,255,0)"
                                })
                            }
                        }, mouseover: function (a) {
                            var c = f(a.virginNode);
                            K("dataplotrollover", c, p, void 0, void 0, function () {
                                K("dataplotrollovercancelled", c, p)
                            })
                        }, mouseout: function (a) {
                            var c = f(a.virginNode),
                                b = x.graphics.singleTracker;
                            b && b.attr({
                                x: 0,
                                y: 0,
                                width: 0,
                                height: 0,
                                stroke: "rgba(255,255,255,0)"
                            });
                            K("dataplotrollout", f(a.virginNode), p, void 0, void 0,
                                function () {
                                    K("dataplotrolloutcancelled", c, p)
                                })
                        }
                    }
                };
                a.setMaxDepth = function (a) {
                    return k = a
                };
                a.getVisibleRoot = function () {
                    return n
                };
                a.setVisibleRoot = function (a) {
                    n = a
                };
                a.visibilityController = b;
                return a
            };
            ja = function (a, b) {
                function d() {
                    B.apply(this, arguments)
                }

                function c(c, b, v) {
                    z = new d(c, A, b);
                    c = z.get();
                    !1 !== v && (m = c);
                    a.setVisibleRoot(c);
                    return c
                }

                function t() {
                    var c = n[w],
                        d;
                    b.realTimeUpdate = k.apply(this, arguments);
                    d = Array.prototype.slice.call(arguments, 0);
                    d.unshift(c);
                    c.drawTree.apply(a.getVisibleRoot(), d)
                }

                function k() {
                    var a,
                        c, b = n[w];
                    c = Array.prototype.slice.call(arguments, 0);
                    c.unshift(b);
                    a = c.slice(-1)[0];
                    return function () {
                        var d = Array.prototype.slice.call(arguments, 0),
                            f = d.shift(),
                            e = d.shift();
                        ga(m, function (a) {
                            b.drawTree.apply(a || m, c)
                        }, a, f)[e].apply(this, d)
                    }
                }
                var n, B = a.AbstractTreeMaker,
                    w, z, m, A, f, h;
                n = {
                    sliceanddice: {
                        areaBaseCalculator: function (a, c) {
                            return function (b, d, f) {
                                var e, g, q = {},
                                    h, k, m, t = e = 0;
                                if (b) {
                                    f && (e = f.textMargin || e);
                                    t = e;
                                    f = b.getParent();
                                    e = b.getSibling("left");
                                    if (f) {
                                        g = f.getValue();
                                        m = f.rect;
                                        h = m.height - 2 * c - t;
                                        k = m.width -
                                            2 * a;
                                        q.effectiveRect = {
                                            height: h,
                                            width: k,
                                            x: m.x + a,
                                            y: m.y + c + t
                                        };
                                        q.effectiveArea = h * k;
                                        q.ratio = b.getValue() / g;
                                        if (e) return d.call(b, q, e, f);
                                        q.lastIsParent = !0;
                                        return d.call(b, q, f)
                                    }
                                    return null
                                }
                            }
                        }, applyShadeFiltering: function (a, c, b) {
                            a.setRangeOutEffect(c, b);
                            this.applyShadeFiltering.reset = function () {
                                a.resetPointers()
                            };
                            return function (c) {
                                a.moveLowerShadePointer(c.start);
                                a.moveHigherShadePointer(c.end)
                            }
                        }, alternateModeManager: function () {
                            return function (a, c) {
                                var b, d, f, e, g, q = a.effectiveArea * a.ratio;
                                d = a.effectiveRect;
                                var h = c.rect;
                                a.lastIsParent ? (e = d.x, g = d.y, b = d.height, d = d.width, f = this.isDirectionVertical = !0) : (b = d.height + d.y - (h.height + h.y), d = d.width + d.x - (h.width + h.x), f = this.isDirectionVertical = !c.isDirectionVertical);
                                f ? (d = q / b, e = void 0 !== e ? e : h.x, g = void 0 !== g ? g : h.y + h.height) : (b = q / d, e = void 0 !== e ? e : h.x + h.width, g = void 0 !== g ? g : h.y);
                                return {
                                    height: b,
                                    width: d,
                                    x: e,
                                    y: g
                                }
                            }
                        }, horizontalVerticalManager: function (a) {
                            var c = "vertical" === a ? !0 : !1;
                            return function (a, b, d) {
                                var e, g, q, l = a.effectiveArea * a.ratio,
                                    f = a.effectiveRect,
                                    h = b.rect;
                                a.lastIsParent ?
                                    (g = f.x, q = f.y, a = f.height, e = f.width, b = this.isDirectionVertical = !b.isDirectionVertical) : (a = f.height + f.y - (h.height + h.y), e = f.width + f.x - (h.width + h.x), b = this.isDirectionVertical = !d.isDirectionVertical);
                                (b = c ? b : !b) ? (0 === a && (a = f.height, g = void 0 !== g ? g : h.x + h.width, q = void 0 !== q ? q : h.y), e = l / a, g = void 0 !== g ? g : h.x, q = void 0 !== q ? q : h.y + h.height) : (0 === e && (e = f.width, g = void 0 !== g ? g : h.x, q = void 0 !== q ? q : h.y + h.height), a = l / e, g = void 0 !== g ? g : h.x + h.width, q = void 0 !== q ? q : h.y);
                                return {
                                    height: a,
                                    width: e,
                                    x: g,
                                    y: q
                                }
                            }
                        }, drawTree: function (c,
                            b, d, p) {
                            var u = b.chart,
                                e = u.components,
                                g = u.config || (u.config = {}),
                                q = g.trackerConfig || (g.trackerConfig = []),
                                m = e.numberFormatter,
                                e = e.toolbarBtns,
                                k = p.drawRect,
                                t = p.drawText,
                                z = p.drawHot,
                                g = d.horizontalPadding,
                                w = d.verticalPadding,
                                A = b.chart.linkedItems.smartLabel,
                                n = a.iterator,
                                B = n(this).df,
                                J, y = c.areaBaseCalculator(g, w),
                                r = b.conf,
                                H = r.highlightParentsOnHover,
                                K, S = a.context,
                                g = a.visibilityController,
                                G = a.mapColorManager(r, b.conf.colorRange),
                                w = a.abstractEventRegisterer.apply(a, arguments),
                                ha = w.click,
                                V = w.mouseover,
                                L = w.mouseout,
                                w = r.slicingMode,
                                M = c["alternate" === w ? "alternateModeManager" : "horizontalVerticalManager"](w),
                                w = u._intSR,
                                E, n = S.getInstance("ClickedState").get("VisibileRoot") || {};
                            (E = n.node) && n.state && ("drillup" === n.state.toLowerCase() ? E instanceof Array ? g.controlPreAnimVisibility(E[0].virginNode, E[1]) : g.controlPreAnimVisibility(E.virginNode) : g.displayAll(n.node.virginNode));
                            K = a.initConfigurationForlabel({
                                x: 5,
                                y: 5
                            }, r.parentLabelLineHeight, r);
                            for (g = J = B.next(h = a.setMaxDepth(this.getDepth() + f)); g.getParent();) g = g.getParent();
                            r.showNavigationBar ? (e.home.hide(), e.back.hide()) : g != J ? (e.home.show(), e.back.show()) : (e.home.hide(), e.back.hide());
                            A.useEllipsesOnOverflow(u.config.useEllipsesWhenOverflow);
                            A.setStyle(r._setStyle = {
                                fontSize: (r.labelFontSize || r.baseFontSize) + "px",
                                fontFamily: r.labelFont || r.baseFont,
                                lineHeight: 1.2 * (r.labelFontSize || r.baseFontSize) + "px"
                            });
                            u = w.backToParent;
                            g = w.resetTree;
                            e.back && e.back.attachEventHandlers({
                                click: u.bind(J)
                            });
                            e.home && e.home.attachEventHandlers({
                                click: g.bind(J)
                            });
                            (function U(a, c) {
                                var b, d, e,
                                    g, f, l, x, v;
                                l = {};
                                var u, w, n = {},
                                    P = {};
                                x = {};
                                var Q = "",
                                    E, O;
                                a && (E = m.yAxis(a.getValue()), O = m.sYAxis(a.getColorValue()), a.setPath(), b = a.rect || {}, d = a.textRect || {}, e = a.rect = {}, x = a.textRect = {}, e.width = c.width, e.height = c.height, e.x = c.x, e.y = c.y, x = G(a), (w = a.plotItem) && p.graphicPool(!0, "plotItem", w, b), w = a.plotItem = k(e, x, b, a.overAttr), a.cssConf = x, v = K(a, e), g = v.conf, l.textMargin = g.rectShiftY, x = a.textRect = g.textRect, u = A.getSmartText(g.label, x.width, x.height).text, a.plotItem = w, (g = a.labelItem) ? (f = a.highlightItem, p.graphicPool(!0,
                                        "labelItem", g, b), p.graphicPool(!0, "highlightItem", f, b)) : d = d || {}, d = t(u, x, {
                                        textAttrs: v.attr,
                                        highlightAttrs: v.highlight
                                    }, d, a.overAttr), a.labelItem = d.label, a.highlightItem = d.highlightMask, n.virginNode = a, n.plotItem = w, n.textItem = d, n.virginNode.dirtyNode = n, a.getColorValue() && (Q = r.tooltipSeparationCharacter + O), n.toolText = r.showTooltip ? D.parseTooltext(r.plotToolText, [1, 2, 3, 119, 122], {
                                        label: a.getLabel(),
                                        formattedValue: E,
                                        formattedsValue: O
                                    }, {
                                        value: a.getValue(),
                                        svalue: a.getColorValue()
                                    }) || a.getLabel() + r.tooltipSeparationCharacter +
                                    E + Q : ca, n.rect = e, P.hover = [
                                        function (a) {
                                            var b, c, d;
                                            d = S.getInstance("hover");
                                            c = this.virginNode;
                                            b = H && !c.next ? (b = c.getParent()) ? b : c : this.virginNode;
                                            d.set("element", b);
                                            d = ba(pa(b.cssConf.fill, 80), 60);
                                            a.attr({
                                                fill: d
                                            });
                                            V(this)
                                        }.bind(n),
                                        function () {
                                            var a, b;
                                            a = S.getInstance("hover").get("element");
                                            b = ba(a.cssConf.fill || "#fff", 0);
                                            a.plotItem.tracker.attr({
                                                fill: b
                                            });
                                            L(this)
                                        }.bind(n)
                                    ], P.tooltip = [n.toolText], P.click = [
                                        function () {
                                            ha(this, J)
                                        }.bind(n)
                                    ], (e = a.hotItem) && p.graphicPool(!0, "hotItem", e, b), q.push({
                                        node: a,
                                        key: "hotItem",
                                        plotDetails: n,
                                        evtFns: P,
                                        callback: z
                                    }), b = B.next(h), l = y(b, M, l), U(b, l))
                            })(J, d)
                        }
                    },
                    squarified: {
                        orderNodes: function () {
                            return this.sort(function (a, b) {
                                return parseFloat(a.value, 10) < parseFloat(b.value, 10) ? 1 : -1
                            })
                        }, areaBaseCalculator: function (a, b) {
                            return function (c, d, f) {
                                var e = {},
                                    g, q = g = 0,
                                    h, m;
                                if (c && 0 !== c.length) return f && (g = f.textMargin || g), q = g, h = c[0], (c = h.getParent()) ? (m = c.rect, f = m.height - 2 * b - q, g = m.width - 2 * a, e.effectiveRect = {
                                    height: f,
                                    width: g,
                                    x: m.x + a,
                                    y: m.y + b + q
                                }, e.effectiveArea = f * g, d.call(h, e, c)) : null
                            }
                        }, layoutManager: function () {
                            function a(b,
                                c) {
                                this.totalValue = c;
                                this._rHeight = b.height;
                                this._rWidth = b.width;
                                this._rx = b.x;
                                this._ry = b.y;
                                this._rTotalArea = b.height * b.width;
                                this.nodes = [];
                                this._prevAR = void 0;
                                this._rHeight < this._rWidth && (this._hSegmented = !0)
                            }
                            a.prototype.constructor = a;
                            a.prototype.addNode = function (a) {
                                var b = this._rTotalArea,
                                    c, d, e, g, f, h, l, m = this._hSegmented,
                                    k = this._rx,
                                    n = this._ry,
                                    w, t, z, A, B = 0;
                                this.nodes.push(a);
                                e = 0;
                                for (d = this.nodes.length; e < d; e++) B += parseFloat(this.nodes[e].getValue(), 10);
                                c = B / this.totalValue * b;
                                m ? (b = this._rHeight, d = c / b,
                                    w = k + d, t = n, z = this._rHeight, A = this._rWidth - d) : (d = this._rWidth, b = c / d, w = k, t = n + b, z = this._rHeight - b, A = this._rWidth);
                                e = 0;
                                for (g = this.nodes.length; e < g; e++) a = this.nodes[e], f = a.getValue(), h = f / B * c, a.hRect = a.rect || {}, a._hRect = a._rect || {}, f = a.rect = {}, m ? (f.width = l = d, f.height = l = h / l, f.x = k, f.y = n, n += l) : (f.height = l = b, f.width = l = h / l, f.x = k, f.y = n, k += l), h = W(f.height, f.width), f = ma(f.height, f.width), a.aspectRatio = h / f;
                                if (1 < this.nodes.length) {
                                    if (this.prevAR < a.aspectRatio) {
                                        this.nodes.pop().rect = {};
                                        e = 0;
                                        for (d = this.nodes.length; e <
                                            d; e++) this.nodes[e].rect = 1 === d && this.nodes[e].firstPassed ? this.nodes[e]._hRect : this.nodes[e].hRect, m = this.nodes[e]._rect = {}, k = this.nodes[e].rect, m.width = k.width, m.height = k.height, m.x = k.x, m.y = k.y;
                                        return !1
                                    }
                                } else a && (m = a._rect = {}, k = a.rect, m.width = k.width, m.height = k.height, m.x = k.x, m.y = k.y, a.firstPassed = !0);
                                this.prevAR = a.aspectRatio;
                                this.height = b;
                                this.width = d;
                                this.getNextLogicalDivision = function () {
                                    return {
                                        height: z,
                                        width: A,
                                        x: w,
                                        y: t
                                    }
                                };
                                return a
                            };
                            return {
                                RowLayout: a
                            }
                        }(),
                        applyShadeFiltering: function (a, b, c) {
                            a.setRangeOutEffect(b,
                                c);
                            this.applyShadeFiltering.reset = function () {
                                a.resetPointers()
                            };
                            return function (b) {
                                a.moveLowerShadePointer(b.start);
                                a.moveHigherShadePointer(b.end)
                            }
                        }, drawTree: function (b, c, d, m) {
                            var k = c.chart,
                                e = k.config || (k.config = {}),
                                g = e.trackerConfig || (e.trackerConfig = []),
                                e = k.components,
                                q = e.numberFormatter,
                                e = e.toolbarBtns,
                                n = b.areaBaseCalculator(d.horizontalPadding, d.verticalPadding),
                                w = b.layoutManager.RowLayout,
                                t = c.chart.linkedItems.smartLabel,
                                z = m.drawRect,
                                A = m.drawText,
                                B = m.drawHot,
                                r = a.iterator,
                                y = r(this).bf,
                                J, F = c.conf,
                                K = F.highlightParentsOnHover,
                                H, O = a.context,
                                S = a.mapColorManager(F, c.conf.colorRange),
                                r = a.abstractEventRegisterer.apply(a, arguments),
                                G = r.click,
                                ha = r.mouseover,
                                V = r.mouseout,
                                r = k._intSR,
                                L = a.visibilityController,
                                M, E;
                            M = O.getInstance("ClickedState").get("VisibileRoot") || {};
                            (E = M.node) && M.state && ("drillup" === M.state.toLowerCase() ? E instanceof Array ? L.controlPreAnimVisibility(E[0].virginNode, E[1]) : L.controlPreAnimVisibility(E.virginNode) : L.displayAll(M.node.virginNode));
                            H = a.initConfigurationForlabel({
                                    x: 5,
                                    y: 5
                                },
                                F.parentLabelLineHeight, F);
                            for (y = J = y.next(h = a.setMaxDepth(this.getDepth() + f)); y.getParent();) y = y.getParent();
                            F.showNavigationBar ? (e.home.hide(), e.back.hide()) : y != J ? (e.home.show(), e.back.show()) : (e.home.hide(), e.back.hide());
                            t.useEllipsesOnOverflow(k.config.useEllipsesWhenOverflow);
                            t.setStyle(F._setStyle = {
                                fontSize: (F.labelFontSize || F.baseFontSize) + "px",
                                fontFamily: F.labelFont || F.baseFont,
                                lineHeight: 1.2 * (F.labelFontSize || F.baseFontSize) + "px"
                            });
                            k = r.backToParent;
                            r = r.resetTree;
                            e.back && e.back.attachEventHandlers({
                                click: k.bind(J)
                            });
                            e.home && e.home.attachEventHandlers({
                                click: r.bind(J)
                            });
                            (function U(a, b) {
                                var c, d = {},
                                    e, f, l, k, x, v, u = 0,
                                    r, y, E, L;
                                v = {};
                                var M;
                                r = {};
                                y = {};
                                k = {};
                                var Q = "",
                                    Y, N;
                                if (a) {
                                    Y = q.yAxis(a.getValue());
                                    N = q.sYAxis(a.getColorValue());
                                    a.setPath();
                                    if (c = a.__initRect) d.x = c.x, d.y = c.y, d.width = c.width, d.height = c.height;
                                    l = a.textRect || {};
                                    c = a.rect = a.__initRect = {};
                                    k = a.textRect = {};
                                    c.width = b.width;
                                    c.height = b.height;
                                    c.x = b.x;
                                    c.y = b.y;
                                    k = S(a);
                                    (E = a.plotItem) && m.graphicPool(!0, "plotItem", E, d);
                                    E = a.plotItem = z(c, k, d, a.overAttr);
                                    a.cssConf = k;
                                    M = H(a, c);
                                    e = M.conf;
                                    v.textMargin = e.rectShiftY;
                                    k = a.textRect = e.textRect;
                                    L = t.getSmartText(e.label, k.width, k.height).text;
                                    (f = a.labelItem) ? (e = a.highlightItem, m.graphicPool(!0, "labelItem", f, d), m.graphicPool(!0, "highlightItem", e, d)) : l = l || {};
                                    l = A(L, k, {
                                        textAttrs: M.attr,
                                        highlightAttrs: M.highlight
                                    }, l, a.overAttr);
                                    a.labelItem = l.label;
                                    a.highlightItem = l.highlightMask;
                                    a.plotItem = E;
                                    r.virginNode = a;
                                    r.plotItem = E;
                                    r.textItem = l;
                                    r.virginNode.dirtyNode = r;
                                    a.getColorValue() && (Q = F.tooltipSeparationCharacter + N);
                                    r.toolText = F.showTooltip ?
                                        D.parseTooltext(F.plotToolText, [1, 2, 3, 119, 122], {
                                            label: a.getLabel(),
                                            formattedValue: Y,
                                            formattedsValue: N
                                        }, {
                                            value: a.getValue(),
                                            svalue: a.getColorValue()
                                        }) || a.getLabel() + F.tooltipSeparationCharacter + Y + Q : ca;
                                    r.rect = c;
                                    y.hover = [
                                        function (a) {
                                            var b, c, d;
                                            d = O.getInstance("hover");
                                            c = this.virginNode;
                                            b = K && !c.next ? (b = c.getParent()) ? b : c : this.virginNode;
                                            d.set("element", b);
                                            d = b.cssConf;
                                            d = ba(d.fill && pa(d.fill, 80), 60);
                                            a.attr({
                                                fill: d
                                            });
                                            ha(this)
                                        }.bind(r),
                                        function (a) {
                                            var b;
                                            b = O.getInstance("hover").get("element").cssConf;
                                            b = ba(b.fill ||
                                                "#fff", 0);
                                            a.attr({
                                                fill: b
                                            });
                                            V(this)
                                        }.bind(r)
                                    ];
                                    y.tooltip = [r.toolText];
                                    y.click = [
                                        function () {
                                            G(this, J)
                                        }.bind(r)
                                    ];
                                    (c = a.hotItem) && m.graphicPool(!0, "hotItem", c, d);
                                    g.push({
                                        node: a,
                                        key: "hotItem",
                                        plotDetails: r,
                                        evtFns: y,
                                        callback: B
                                    });
                                    if (x = void 0 !== h ? a.getDepth() >= h ? void 0 : a.getChildren() : a.getChildren())
                                        for (r = n(x, function (a, b) {
                                            var c, d, e = 0,
                                                g, f, l = [];
                                            c = new w({
                                                width: a.effectiveRect.width,
                                                height: a.effectiveRect.height,
                                                x: a.effectiveRect.x,
                                                y: a.effectiveRect.y
                                            }, b.getValue());
                                            for (d = x.length; e++ !== d;) g = x[e - 1], f = c.addNode(g), !1 === f ? (c = c.getNextLogicalDivision(), c = new w(c, b.getValue() - u), e--) : (u += parseFloat(g.getValue(), 10), l.push(g));
                                            return l
                                        }, v), d = 0, v = r.length; d < v; d++) y = r[d], U(y, y.rect)
                                }
                            })(J, d)
                        }
                    }
                };
                d.prototype = Object.create(B.prototype);
                d.prototype.constructor = B;
                d.prototype.order = function (a) {
                    var b = n[w],
                        c = b.orderNodes;
                    return c ? c.apply(a, [b]) : a
                };
                b.init = function (b, c, d) {
                    w = b;
                    A = c;
                    f = a.setMaxDepth(d);
                    return n[w]
                };
                b.plotOnCanvas = function (a, b, d) {
                    m = c(a, d);
                    return t
                };
                b.applyShadeFiltering = function (a, b) {
                    var c, d;
                    d = n[w].applyShadeFiltering(z.getBucket(),
                        a, b);
                    return function (a) {
                        c = Array.prototype.slice.call(arguments, 0);
                        c.unshift(a);
                        d.apply(z.getBucket(), c)
                    }
                };
                b.setTreeBase = function (a) {
                    return a && (m = a)
                };
                b.realTimeUpdate = k;
                b.makeTree = c;
                return b
            };
            ga = function (a, b, d, c) {
                function t(b) {
                    var c, d = 0;
                    c = a;
                    if (!b.length) return a;
                    for (; c;) {
                        c = k.call(c, b[d]);
                        if (d === b.length - 1 && c) return n = c.getValue(), c;
                        d += 1
                    }
                }

                function k(a) {
                    var b, c, d, k = this.getChildren() || [],
                        f = k.length;
                    for (b = 0; b < f; b += 1)
                        if (d = k[b], d.label.toLowerCase().trim() === a.toLowerCase().trim()) {
                            c = d;
                            break
                        }
                    return c
                }
                var n;
                return {
                    deleteData: function (a, c) {
                        var k = t(a),
                            m = (void 0).iterator(k).df,
                            r = k && k.getParent(),
                            f = k && k.getSiblingCount("left"),
                            h = r && r.getChildren(),
                            l = (void 0).getVisibleRoot();
                        if (k && r) {
                            h.splice(f, 1);
                            for (k === l && (l = k.getParent() || l); k;) d.disposeItems(k), k = m.next();
                            for (; r;) r.setValue(-n, !0), r = r.getParent();
                            c && b(l)
                        }
                    }, addData: function (a, d, k, m) {
                        for (var n, f, h, l = 0, r = !0, v = (void 0).getVisibleRoot(); a.length;)
                            if (n = a.pop(), n = (void 0).makeTree(n, c, !1), l = n.getValue(), f = t(d || []))
                                for (f.getChildren() || (h = f.getValue(), r = !1),
                                    f.addChildren(n, m); f;) f.setValue(l, r), h && (l -= h, h = void 0, r = !0), f = f.getParent();
                        k && b(v)
                    }
                }
            };
            ka = function (a, b, d) {
                function c() {}

                function r(b) {
                    var c = m.plotBorderThickness;
                    w.apply(a.getVisibleRoot(), [B, {
                            width: b.effectiveWidth,
                            height: b.effectiveHeight,
                            x: b.startX,
                            y: b.startY,
                            horizontalPadding: m.horizontalPadding,
                            verticalPadding: m.verticalPadding
                        },
                        A
                    ]);
                    m.plotBorderThickness = c
                }

                function k(a, b, c) {
                    var d = a.width,
                        f = a.height,
                        h = m.seperatorAngle / 2;
                    a = ["M", a.x, a.y];
                    var k = y(h ? f / 2 * (1 - sa(h)) : c, 15);
                    c = function (a) {
                        return {
                            both: ["h",
                                d, "v", a, "h", -d, "v", -a
                            ],
                            right: ["h", d, "v", a, "h", -d, "l", k, -a / 2, "l", -k, -a / 2],
                            no: ["h", d, "l", k, a / 2, "l", -k, a / 2, "h", -d, "l", k, -a / 2, "l", -k, -a / 2],
                            left: ["h", d, "l", k, a / 2, "l", -k, a / 2, "h", -d, "v", -a]
                        }
                    };
                    return {
                        path: a.concat(c(f)[b]),
                        _path: a.concat(c(0)[b]),
                        offset: k
                    }
                }

                function n() {
                    var a = Array.prototype.splice.call(arguments, 0);
                    a.push(!0);
                    l("navigationBar").apply(this, a)
                }
                var B, w, z, m, A, f, h = function (b, c) {
                        var f, h, l = a.mapColorManager(m, m.colorRange, !0),
                            n = function () {
                                var a;
                                return {
                                    get: function (c, b, d) {
                                        var e = {
                                            y: c.startY,
                                            height: c.effectiveHeight
                                        };
                                        b = f[b];
                                        var g = b.getParent();
                                        e.x = a || (a = c.startX);
                                        a = d ? a + (e.width = c.effectiveWidth * (b.getValue() / g.getValue())) : a + (e.width = c.effectiveWidth / h);
                                        return e
                                    }, resetAllocation: function () {
                                        a = void 0
                                    }
                                }
                            }(),
                            r = a.initConfigurationForlabel({
                                x: 5,
                                y: 5
                            }, m.parentLabelLineHeight, m),
                            t = A.drawPolyPath,
                            v = A.drawText,
                            w = A.drawHot,
                            u = {
                                navigationHistory: {
                                    path: "polyPathItem",
                                    label: "pathlabelItem",
                                    highlightItem: "pathhighlightItem",
                                    hotItem: "pathhotItem"
                                }
                            },
                            y = B.chart,
                            F = y.config.trackerConfig,
                            D = y.components.gradientLegend,
                            y = y.linkedItems.smartLabel,
                            H = function (c) {
                                return function () {
                                    var b = a.context.getInstance("ClickedState").get("VisibileRoot") || {};
                                    F.length = 0;
                                    b.state = "drillup";
                                    b.node = [{
                                            virginNode: a.getVisibleRoot()
                                        },
                                        c
                                    ];
                                    D && D.enabled && D.resetLegend();
                                    d.draw([c, c, c])
                                }
                            },
                            K = function () {
                                return function () {}
                            },
                            O = function () {
                                return function () {}
                            },
                            G, N, V, L, M, E, T = m._setStyle,
                            U;
                        G = x.get().navigationBar;
                        L = 2 * p("navigationBar");
                        T = ma(G * z.effectiveHeight - (L + 6), T.fontSize.replace(/\D+/g, ""));
                        G = T + "px";
                        u.stacked = {
                            path: "stacked" + u.navigationHistory.path,
                            label: "stacked" +
                                u.navigationHistory.label,
                            highlightItem: "stacked" + u.navigationHistory.highlightItem,
                            hotItem: "stacked" + u.navigationHistory.hotItem
                        };
                        n.resetAllocation();
                        (function (c) {
                            var b = a.getVisibleRoot();
                            f = c ? b.getChildren() : b.getPath() || [].concat(b);
                            f.pop();
                            h = f.length
                        })(c);
                        y.setStyle({
                            fontSize: G,
                            lineHeight: G
                        });
                        for (G = 0; G < h; G += 1) L = f[G], M = n.get(b, G, c), N = (V = k(M, c ? "both" : 1 === h ? "both" : 0 === G ? "left" : G < h - 1 ? "no" : "right")).offset, L[u[c ? "stacked" : "navigationHistory"].path] = t(V, l(L, !0, !0), G), V = r(L, M, !1, !0), E = V.conf, U = E.textRect,
                            U.width -= 2 * N, U.y = M.y + M.height / 2, N = y.getSmartText(E.label, U.width, W(T, U.height)).text, N = v(N, U, {
                                textAttrs: V.attr,
                                highlightAttrs: V.highlight
                            }, {
                                y: M.height / 10,
                                "font-size": m._setStyle.fontSize,
                                "font-family": m._setStyle.fontFamily
                            }, (c ? "stacked" : "") + "path"), L[u[c ? "stacked" : "navigationHistory"].label] = N.label, L[u[c ? "stacked" : "navigationHistory"].highlightItem] = N.highlightMask, F.push({
                                node: L,
                                key: u[c ? "stacked" : "navigationHistory"].hotItem,
                                plotDetails: {
                                    rect: M
                                },
                                evtFns: {
                                    click: [H(L, c)],
                                    hover: [K(L), O()],
                                    tooltip: [m.showTooltip ?
                                        L.getLabel() : ca
                                    ]
                                },
                                callback: w
                            })
                    },
                    l = function (a) {
                        return {
                            treeMap: r,
                            navigationBar: h,
                            stackedNavigation: n
                        }[a]
                    },
                    x = function () {
                        var a = {
                            treeMap: 1,
                            navigationBar: 0,
                            stackedNavigation: 0
                        };
                        return {
                            set: function (c) {
                                var b = y(m.navigationBarHeightRatio, m.navigationBarHeight / z.effectiveHeight, .15),
                                    d = m.labelFontSize ? W(m.labelFontSize, m.baseFontSize) : m.baseFontSize,
                                    f = 2 * p("navigationBar"),
                                    b = W((6 + d + f) / z.effectiveHeight, b);.1 > b ? b = .1 : .15 < b && (b = .15);
                                m.navigationBarHeightRatio = b;
                                a = c ? {
                                    treeMap: 1 - b,
                                    navigationBar: b,
                                    stackedNavigation: 0
                                } : {
                                    treeMap: 1,
                                    navigationBar: 0,
                                    stackedNavigation: 0
                                }
                            }, get: function () {
                                return a
                            }
                        }
                    }(),
                    v = 0,
                    p = function (a) {
                        var b = m.plotBorderThickness,
                            c = m.navigationBarBorderThickness;
                        return m.verticalPadding + ("navigationBar" === a ? c : b)
                    },
                    u = function (a) {
                        var b = z.effectiveWidth,
                            c = z.effectiveHeight,
                            d = p(a);
                        a = x.get()[a];
                        1 <= v && (v = 0);
                        v += a;
                        return {
                            effectiveHeight: la(a * c * 100) / 100 - d,
                            effectiveWidth: b,
                            startX: z.startX,
                            startY: z.startY + d + la((v - a) * c * 100) / 100
                        }
                    };
                c.prototype.constructor = c;
                c.prototype.init = function (a, b) {
                    (this.conf || (this.conf = {})).name =
                        a.name;
                    this.setDrawingArea(a.drawingAreaMeasurement);
                    this.draw = this.draw(b)
                };
                c.prototype.setDrawingArea = function (a) {
                    this.conf.drawingAreaMeasurement = a
                };
                c.prototype.draw = function (a) {
                    return function () {
                        var b = this.conf;
                        0 < b.drawingAreaMeasurement.effectiveHeight && a(b.drawingAreaMeasurement)
                    }
                };
                c.prototype.eventCallback = function () {};
                f = function () {
                    var a = [];
                    return {
                        get: function () {
                            return a
                        }, set: function (b) {
                            var d;
                            b ? (d = new c, d.init({
                                    name: b.type,
                                    drawingAreaMeasurement: b.drawingArea
                                }, b.drawFn), a.push(d)) : a.length =
                                0;
                            return a
                        }
                    }
                }();
                d.init = function () {
                    var a, b = ["navigationBar", "treeMap", "stackedNavigation"];
                    a = Array.prototype.slice.call(arguments, 0);
                    B = a[0];
                    z = a[1];
                    m = B.conf;
                    A = a[2];
                    w = a[4];
                    for (f.get().length >= b.length && f.set(); b.length;) a = b.shift(), f.set({
                        type: a,
                        drawFn: l(a),
                        drawingArea: u(a)
                    })
                };
                d.draw = function (b) {
                    var c, h, k;
                    c = a.getVisibleRoot();
                    A.disposeChild(c);
                    b && (c = b[1]);
                    c.getParent() ? m.showNavigationBar && d.heightProportion.set(!0) : d.heightProportion.set(!1);
                    h = f.get();
                    for (c = 0; c < h.length; c += 1) k = h[c], k.setDrawingArea(u(k.conf.name)),
                        b && a.setVisibleRoot(b[c]), k.draw()
                };
                d.heightProportion = x;
                d.remove = function () {
                    var b = a.getVisibleRoot();
                    b && A.disposeChild(b)
                };
                return d
            }
        }
    ])
});

//# sourceMappingURL=http://localhost:3052/3.12.2/map/eval/fusioncharts.treemap.js.map